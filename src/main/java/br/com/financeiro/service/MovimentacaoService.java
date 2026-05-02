package br.com.financeiro.service;

import br.com.financeiro.dao.MovimentacaoDAO;
import br.com.financeiro.model.Movimentacao;
import br.com.financeiro.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MovimentacaoService {

    private final MovimentacaoDAO movDAO = new MovimentacaoDAO();

    public List<Movimentacao> extratoPorConta(Long contaId) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            List<Movimentacao> lista = movDAO.listarPorConta(conn, contaId);
            conn.commit();
            return lista;
        }
    }

    public List<Movimentacao> listarRecentes(int limite) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            List<Movimentacao> lista = movDAO.listarRecentes(conn, limite);
            conn.commit();
            return lista;
        }
    }
}
