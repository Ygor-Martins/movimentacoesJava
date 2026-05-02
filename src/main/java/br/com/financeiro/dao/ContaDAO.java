package br.com.financeiro.dao;

import br.com.financeiro.model.Conta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Acesso a dados da entidade Conta via JDBC puro.
 * Toda query usa PreparedStatement — nenhuma concatenação de dados do usuário.
 */
public class ContaDAO {

    // ── Inserção ──────────────────────────────────────────────

    public Conta inserir(Connection conn, Conta conta) throws SQLException {
        final String sql =
                "INSERT INTO conta (numero, titular, tipo, saldo, ativa) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, conta.getNumero());
            ps.setString(2, conta.getTitular());
            ps.setString(3, conta.getTipo().name());
            ps.setBigDecimal(4, conta.getSaldo());
            ps.setBoolean(5, true);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    conta.setId(rs.getLong(1));
                }
            }
        }
        return conta;
    }

    // ── Atualização ───────────────────────────────────────────

    public void atualizar(Connection conn, Conta conta) throws SQLException {
        final String sql =
                "UPDATE conta SET titular = ?, tipo = ?, ativa = ? " +
                "WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, conta.getTitular());
            ps.setString(2, conta.getTipo().name());
            ps.setBoolean(3, conta.isAtiva());
            ps.setLong(4, conta.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Atualiza APENAS o saldo — usado dentro de transações de transferência.
     * Recebe a própria Connection da transação em andamento.
     */
    public void atualizarSaldo(Connection conn, Long contaId, java.math.BigDecimal novoSaldo)
            throws SQLException {
        final String sql = "UPDATE conta SET saldo = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, novoSaldo);
            ps.setLong(2, contaId);
            int linhas = ps.executeUpdate();
            if (linhas == 0) {
                throw new SQLException("Conta id=" + contaId + " não encontrada ao atualizar saldo.");
            }
        }
    }

    // ── Consultas ─────────────────────────────────────────────

    public Optional<Conta> buscarPorId(Connection conn, Long id) throws SQLException {
        final String sql = "SELECT * FROM conta WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Conta> buscarPorNumero(Connection conn, String numero) throws SQLException {
        final String sql = "SELECT * FROM conta WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Conta> listarTodas(Connection conn) throws SQLException {
        final String sql = "SELECT * FROM conta ORDER BY titular";
        List<Conta> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Conta> listarAtivas(Connection conn) throws SQLException {
        final String sql = "SELECT * FROM conta WHERE ativa = TRUE ORDER BY titular";
        List<Conta> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ── Mapeamento ResultSet → Model ──────────────────────────

    private Conta mapear(ResultSet rs) throws SQLException {
        Conta c = new Conta();
        c.setId(rs.getLong("id"));
        c.setNumero(rs.getString("numero"));
        c.setTitular(rs.getString("titular"));
        c.setTipo(Conta.Tipo.valueOf(rs.getString("tipo")));
        c.setSaldo(rs.getBigDecimal("saldo"));
        c.setAtiva(rs.getBoolean("ativa"));
        Timestamp ts = rs.getTimestamp("criada_em");
        if (ts != null) c.setCriadaEm(ts.toLocalDateTime());
        return c;
    }
}
