package br.com.financeiro.dao;

import br.com.financeiro.model.Movimentacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso a dados de Movimentacao via JDBC puro.
 * Recebe sempre a Connection como parâmetro para participar de transações externas.
 */
public class MovimentacaoDAO {

    public Movimentacao inserir(Connection conn, Movimentacao mov) throws SQLException {
        final String sql =
                "INSERT INTO movimentacao " +
                "    (conta_id, tipo, valor, descricao, conta_origem_id, conta_destino_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, mov.getContaId());
            ps.setString(2, mov.getTipo().name());
            ps.setBigDecimal(3, mov.getValor());
            ps.setString(4, mov.getDescricao());
            setNullableLong(ps, 5, mov.getContaOrigemId());
            setNullableLong(ps, 6, mov.getContaDestinoId());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    mov.setId(rs.getLong(1));
                }
            }
        }
        return mov;
    }

    /** Extrato de uma conta específica — ordenado do mais recente ao mais antigo. */
    public List<Movimentacao> listarPorConta(Connection conn, Long contaId) throws SQLException {
        final String sql =
                "SELECT m.*, " +
                "       co.numero AS num_origem, " +
                "       cd.numero AS num_destino " +
                "FROM movimentacao m " +
                "LEFT JOIN conta co ON co.id = m.conta_origem_id " +
                "LEFT JOIN conta cd ON cd.id = m.conta_destino_id " +
                "WHERE m.conta_id = ? " +
                "ORDER BY m.realizada_em DESC " +
                "LIMIT 100";
        List<Movimentacao> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, contaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /** Últimas movimentações globais para o painel. */
    public List<Movimentacao> listarRecentes(Connection conn, int limite) throws SQLException {
        final String sql =
                "SELECT m.*, " +
                "       co.numero AS num_origem, " +
                "       cd.numero AS num_destino " +
                "FROM movimentacao m " +
                "LEFT JOIN conta co ON co.id = m.conta_origem_id " +
                "LEFT JOIN conta cd ON cd.id = m.conta_destino_id " +
                "ORDER BY m.realizada_em DESC " +
                "LIMIT ?";
        List<Movimentacao> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // ── Helpers ───────────────────────────────────────────────

    private void setNullableLong(PreparedStatement ps, int idx, Long value) throws SQLException {
        if (value != null) ps.setLong(idx, value);
        else               ps.setNull(idx, Types.BIGINT);
    }

    private Movimentacao mapear(ResultSet rs) throws SQLException {
        Movimentacao m = new Movimentacao();
        m.setId(rs.getLong("id"));
        m.setContaId(rs.getLong("conta_id"));
        m.setTipo(Movimentacao.Tipo.valueOf(rs.getString("tipo")));
        m.setValor(rs.getBigDecimal("valor"));
        m.setDescricao(rs.getString("descricao"));

        long origemId = rs.getLong("conta_origem_id");
        if (!rs.wasNull()) m.setContaOrigemId(origemId);

        long destinoId = rs.getLong("conta_destino_id");
        if (!rs.wasNull()) m.setContaDestinoId(destinoId);

        Timestamp ts = rs.getTimestamp("realizada_em");
        if (ts != null) m.setRealizadaEm(ts.toLocalDateTime());

        m.setNumeroContaOrigem(rs.getString("num_origem"));
        m.setNumeroContaDestino(rs.getString("num_destino"));
        return m;
    }
}
