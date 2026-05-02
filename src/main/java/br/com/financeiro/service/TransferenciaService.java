package br.com.financeiro.service;

import br.com.financeiro.dao.ContaDAO;
import br.com.financeiro.dao.MovimentacaoDAO;
import br.com.financeiro.model.Conta;
import br.com.financeiro.model.Movimentacao;
import br.com.financeiro.util.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Serviço de transferência entre contas.
 *
 * <p><b>Garantias transacionais:</b>
 * <ul>
 *   <li>Uma única Connection JDBC compartilhada entre débito, crédito e registro das movimentações.</li>
 *   <li>commit() explícito ao final — rollback() explícito em qualquer falha.</li>
 *   <li>autoCommit desabilitado (configurado no pool).</li>
 * </ul>
 */
public class TransferenciaService {

    private final ContaDAO         contaDAO  = new ContaDAO();
    private final MovimentacaoDAO  movDAO    = new MovimentacaoDAO();

    /**
     * Executa a transferência de {@code valor} da conta origem para a conta destino.
     *
     * @return array com [movSaida, movEntrada]
     */
    public Movimentacao[] transferir(Long origemId, Long destinoId,
                                     BigDecimal valor, String descricao)
            throws RegraNegocioException, SQLException {

        // ── Validações de negócio (antes de abrir conexão) ──────────
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor da transferência deve ser maior que zero.");
        }
        if (origemId == null || destinoId == null) {
            throw new RegraNegocioException("Conta de origem e destino são obrigatórias.");
        }
        if (origemId.equals(destinoId)) {
            throw new RegraNegocioException("Conta de origem e destino não podem ser a mesma.");
        }
        if (descricao != null && descricao.length() > 255) {
            throw new RegraNegocioException("Descrição deve ter no máximo 255 caracteres.");
        }

        // ── Transação JDBC ───────────────────────────────────────────
        Connection conn = ConnectionFactory.getConnection();
        try {
            // autoCommit já vem false pelo pool — garantia extra:
            conn.setAutoCommit(false);

            // 1. Carrega contas com lock de linha (SELECT FOR UPDATE evita race condition)
            Conta origem  = buscarContaAtiva(conn, origemId,  "origem");
            Conta destino = buscarContaAtiva(conn, destinoId, "destino");

            // 2. Valida saldo suficiente
            if (origem.getSaldo().compareTo(valor) < 0) {
                throw new RegraNegocioException(
                        String.format("Saldo insuficiente. Disponível: R$ %.2f | Solicitado: R$ %.2f",
                                origem.getSaldo(), valor));
            }

            // 3. Calcula novos saldos
            BigDecimal novoSaldoOrigem  = origem.getSaldo().subtract(valor);
            BigDecimal novoSaldoDestino = destino.getSaldo().add(valor);

            // 4. Atualiza saldos
            contaDAO.atualizarSaldo(conn, origemId,  novoSaldoOrigem);
            contaDAO.atualizarSaldo(conn, destinoId, novoSaldoDestino);

            // 5. Registra movimentação de saída (débito na origem)
            Movimentacao movSaida = new Movimentacao();
            movSaida.setContaId(origemId);
            movSaida.setTipo(Movimentacao.Tipo.TRANSFERENCIA_SAIDA);
            movSaida.setValor(valor);
            movSaida.setDescricao(descricao);
            movSaida.setContaOrigemId(origemId);
            movSaida.setContaDestinoId(destinoId);
            movDAO.inserir(conn, movSaida);

            // 6. Registra movimentação de entrada (crédito no destino)
            Movimentacao movEntrada = new Movimentacao();
            movEntrada.setContaId(destinoId);
            movEntrada.setTipo(Movimentacao.Tipo.TRANSFERENCIA_ENTRADA);
            movEntrada.setValor(valor);
            movEntrada.setDescricao(descricao);
            movEntrada.setContaOrigemId(origemId);
            movEntrada.setContaDestinoId(destinoId);
            movDAO.inserir(conn, movEntrada);

            // ── COMMIT explícito ─────────────────────────────────────
            conn.commit();

            return new Movimentacao[]{movSaida, movEntrada};

        } catch (RegraNegocioException | SQLException ex) {
            // ── ROLLBACK explícito ───────────────────────────────────
            try { conn.rollback(); } catch (SQLException rollEx) {
                // Loga mas propaga a exceção original
                rollEx.printStackTrace();
            }
            throw ex;
        } finally {
            try { conn.close(); } catch (SQLException ignore) {}
        }
    }

    // ── Helper ────────────────────────────────────────────────

    private Conta buscarContaAtiva(Connection conn, Long id, String papel)
            throws SQLException, RegraNegocioException {
        return contaDAO.buscarPorId(conn, id)
                .filter(Conta::isAtiva)
                .orElseThrow(() -> new RegraNegocioException(
                        "Conta de " + papel + " não encontrada ou está inativa."));
    }
}
