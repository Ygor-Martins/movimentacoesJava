package br.com.financeiro.servlet;

import br.com.financeiro.model.Conta;
import br.com.financeiro.model.Movimentacao;
import br.com.financeiro.service.ContaService;
import br.com.financeiro.service.MovimentacaoService;
import br.com.financeiro.service.RegraNegocioException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/movimentacoes/*")
public class MovimentacaoServlet extends HttpServlet {

    private final MovimentacaoService movService  = new MovimentacaoService();
    private final ContaService        contaService = new ContaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo();

        try {
            if ("/extrato".equals(pathInfo)) {
                extrato(req, resp);
            } else {
                recentes(req, resp);
            }
        } catch (Exception e) {
            throw new ServletException("Erro ao consultar movimentações", e);
        }
    }

    private void extrato(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        String contaIdParam = req.getParameter("contaId");

        if (contaIdParam == null || contaIdParam.isBlank()) {
            List<Conta> contas = contaService.listarTodas();
            req.setAttribute("contas", contas);
            req.getRequestDispatcher("/views/movimentacoes/extrato.jsp").forward(req, resp);
            return;
        }

        Long contaId;
        try { contaId = Long.parseLong(contaIdParam.trim()); }
        catch (NumberFormatException e) {
            throw new RegraNegocioException("ID de conta inválido.");
        }

        Conta conta = contaService.buscarPorId(contaId)
                .orElseThrow(() -> new RegraNegocioException("Conta não encontrada."));

        List<Movimentacao> movimentacoes = movService.extratoPorConta(contaId);
        List<Conta> contas = contaService.listarTodas();

        req.setAttribute("conta",         conta);
        req.setAttribute("movimentacoes", movimentacoes);
        req.setAttribute("contas",        contas);
        req.setAttribute("contaIdSel",    contaId);

        req.getRequestDispatcher("/views/movimentacoes/extrato.jsp").forward(req, resp);
    }

    private void recentes(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        List<Movimentacao> recentes = movService.listarRecentes(20);
        req.setAttribute("movimentacoes", recentes);
        req.getRequestDispatcher("/views/movimentacoes/recentes.jsp").forward(req, resp);
    }
}
