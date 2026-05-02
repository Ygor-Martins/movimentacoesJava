package br.com.financeiro.service;

import br.com.financeiro.dao.ContaDAO;
import br.com.financeiro.model.Conta;
import br.com.financeiro.util.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Camada de serviço para operações de Conta.
 * Centraliza todas as regras de negócio — os Servlets nunca acessam o DAO diretamente.
 */
public class ContaService {

    private final ContaDAO contaDAO = new ContaDAO();

    // ── Cadastro ──────────────────────────────────────────────

    public Conta cadastrar(String numero, String titular, String tipo, BigDecimal saldoInicial)
            throws RegraNegocioException, SQLException {

        validarNumero(numero);
        validarTitular(titular);
        Conta.Tipo tipoEnum = parseTipo(tipo);
        validarSaldoInicial(saldoInicial);

        try (Connection conn = ConnectionFactory.getConnection()) {
            // Garante número único
            if (contaDAO.buscarPorNumero(conn, numero.trim()).isPresent()) {
                throw new RegraNegocioException("Já existe uma conta com o número '" + numero.trim() + "'.");
            }

            Conta conta = new Conta();
            conta.setNumero(numero.trim());
            conta.setTitular(titular.trim());
            conta.setTipo(tipoEnum);
            conta.setSaldo(saldoInicial);
            conta.setAtiva(true);

            contaDAO.inserir(conn, conta);
            conn.commit();
            return conta;
        }
    }

    // ── Edição ────────────────────────────────────────────────

    public Conta editar(Long id, String titular, String tipo, boolean ativa)
            throws RegraNegocioException, SQLException {

        validarTitular(titular);
        Conta.Tipo tipoEnum = parseTipo(tipo);

        try (Connection conn = ConnectionFactory.getConnection()) {
            Conta conta = contaDAO.buscarPorId(conn, id)
                    .orElseThrow(() -> new RegraNegocioException("Conta não encontrada."));

            if (!conta.isAtiva() && ativa) {
                // Reativando — permitido
            }
            if (conta.isAtiva() && !ativa && conta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
                throw new RegraNegocioException(
                        "Não é possível desativar uma conta com saldo diferente de zero.");
            }

            conta.setTitular(titular.trim());
            conta.setTipo(tipoEnum);
            conta.setAtiva(ativa);

            contaDAO.atualizar(conn, conta);
            conn.commit();
            return conta;
        }
    }

    // ── Consultas ─────────────────────────────────────────────

    public List<Conta> listarTodas() throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            List<Conta> lista = contaDAO.listarTodas(conn);
            conn.commit();
            return lista;
        }
    }

    public List<Conta> listarAtivas() throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            List<Conta> lista = contaDAO.listarAtivas(conn);
            conn.commit();
            return lista;
        }
    }

    public Optional<Conta> buscarPorId(Long id) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            Optional<Conta> opt = contaDAO.buscarPorId(conn, id);
            conn.commit();
            return opt;
        }
    }

    // ── Validações privadas ───────────────────────────────────

    private void validarNumero(String numero) throws RegraNegocioException {
        if (numero == null || numero.isBlank()) {
            throw new RegraNegocioException("Número da conta é obrigatório.");
        }
        if (numero.trim().length() > 20) {
            throw new RegraNegocioException("Número da conta deve ter no máximo 20 caracteres.");
        }
    }

    private void validarTitular(String titular) throws RegraNegocioException {
        if (titular == null || titular.isBlank()) {
            throw new RegraNegocioException("Nome do titular é obrigatório.");
        }
        if (titular.trim().length() < 3) {
            throw new RegraNegocioException("Nome do titular deve ter pelo menos 3 caracteres.");
        }
    }

    private Conta.Tipo parseTipo(String tipo) throws RegraNegocioException {
        try {
            return Conta.Tipo.valueOf(tipo);
        } catch (Exception e) {
            throw new RegraNegocioException("Tipo de conta inválido: " + tipo);
        }
    }

    private void validarSaldoInicial(BigDecimal saldo) throws RegraNegocioException {
        if (saldo == null || saldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("Saldo inicial não pode ser negativo.");
        }
    }
}
