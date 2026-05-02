package br.com.financeiro.servlet;

import br.com.financeiro.model.Conta;
import br.com.financeiro.service.ContaService;
import br.com.financeiro.service.RegraNegocioException;
import br.com.financeiro.service.TransferenciaService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/transferencias/*")
public class TransferenciaServlet extends HttpServlet {

    private final TransferenciaService transferenciaService = new TransferenciaService();
    private final ContaService         contaService         = new ContaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Conta> contas = contaService.listarAtivas();
            req.setAttribute("contas", contas);
            req.getRequestDispatcher("/views/transferencias/form.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Erro ao carregar formulário de transferência", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String origemParam  = req.getParameter("origemId");
        String destinoParam = req.getParameter("destinoId");
        String valorParam   = req.getParameter("valor");
        String descricao    = req.getParameter("descricao");

        try {
            Long       origemId  = parseLong(origemParam);
            Long       destinoId = parseLong(destinoParam);
            BigDecimal valor     = parseBigDecimal(valorParam);

            transferenciaService.transferir(origemId, destinoId, valor, descricao);

            req.getSession().setAttribute("sucesso",
                    String.format("Transferência de R$ %.2f realizada com sucesso!", valor));
            resp.sendRedirect(req.getContextPath() + "/transferencias/");

        } catch (RegraNegocioException e) {
            req.setAttribute("erro", e.getMessage());
            repovoaFormulario(req, origemParam, destinoParam, valorParam, descricao);
            req.getRequestDispatcher("/views/transferencias/form.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("Erro interno ao processar transferência", e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────

    private void repovoaFormulario(HttpServletRequest req,
                                   String origemParam, String destinoParam,
                                   String valorParam, String descricao) {
        try {
            List<Conta> contas = contaService.listarAtivas();
            req.setAttribute("contas", contas);
        } catch (Exception ignored) {}

        req.setAttribute("origemSel",  origemParam);
        req.setAttribute("destinoSel", destinoParam);
        req.setAttribute("valorStr",   valorParam);
        req.setAttribute("descricao",  descricao);
    }

    private Long parseLong(String s) throws RegraNegocioException {
        try { return Long.parseLong(s.trim()); }
        catch (Exception e) { throw new RegraNegocioException("Conta inválida selecionada."); }
    }

    private BigDecimal parseBigDecimal(String s) throws RegraNegocioException {
        try { return new BigDecimal(s.replace(",", ".").trim()); }
        catch (Exception e) { throw new RegraNegocioException("Valor inválido: " + s); }
    }
}
