package br.com.financeiro.servlet;

import br.com.financeiro.model.Conta;
import br.com.financeiro.model.Movimentacao;
import br.com.financeiro.service.ContaService;
import br.com.financeiro.service.MovimentacaoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(urlPatterns = {"", "/", "/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final ContaService        contaService = new ContaService();
    private final MovimentacaoService movService   = new MovimentacaoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Conta>         contas   = contaService.listarTodas();
            List<Movimentacao>  recentes = movService.listarRecentes(5);

            BigDecimal totalSaldo = contas.stream()
                    .filter(Conta::isAtiva)
                    .map(Conta::getSaldo)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long totalAtivas = contas.stream().filter(Conta::isAtiva).count();

            req.setAttribute("contas",     contas);
            req.setAttribute("recentes",   recentes);
            req.setAttribute("totalSaldo", totalSaldo);
            req.setAttribute("totalAtivas", totalAtivas);

            req.getRequestDispatcher("/views/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Erro ao carregar dashboard", e);
        }
    }
}
