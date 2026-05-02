package br.com.financeiro.servlet;

import br.com.financeiro.model.Conta;
import br.com.financeiro.service.ContaService;
import br.com.financeiro.service.RegraNegocioException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/contas/*")
public class ContaServlet extends HttpServlet {

    private final ContaService service = new ContaService();

    // ── GET ───────────────────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo(); // null | "/nova" | "/editar" | "/{id}"

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                listar(req, resp);

            } else if (pathInfo.equals("/nova")) {
                req.getRequestDispatcher("/views/contas/form.jsp").forward(req, resp);

            } else if (pathInfo.startsWith("/editar/")) {
                Long id = parseLong(pathInfo.substring("/editar/".length()));
                Conta conta = service.buscarPorId(id)
                        .orElseThrow(() -> new RegraNegocioException("Conta não encontrada."));
                req.setAttribute("conta", conta);
                req.getRequestDispatcher("/views/contas/form.jsp").forward(req, resp);

            } else if (pathInfo.startsWith("/extrato/")) {
                Long id = parseLong(pathInfo.substring("/extrato/".length()));
                resp.sendRedirect(req.getContextPath() + "/movimentacoes/extrato?contaId=" + id);

            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (RegraNegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/views/contas/lista.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Erro ao processar contas", e);
        }
    }

    // ── POST ──────────────────────────────────────────────────

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();

        try {
            if ("/salvar".equals(pathInfo)) {
                salvar(req, resp);
            } else if (pathInfo != null && pathInfo.startsWith("/editar/")) {
                atualizar(req, resp, pathInfo);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (RegraNegocioException e) {
            req.setAttribute("erro", e.getMessage());
            populaFormEdicao(req);
            req.getRequestDispatcher("/views/contas/form.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Erro ao salvar conta", e);
        }
    }

    // ── Ações privadas ────────────────────────────────────────

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        List<Conta> contas = service.listarTodas();
        req.setAttribute("contas", contas);
        req.getRequestDispatcher("/views/contas/lista.jsp").forward(req, resp);
    }

    private void salvar(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        String numero        = req.getParameter("numero");
        String titular       = req.getParameter("titular");
        String tipo          = req.getParameter("tipo");
        BigDecimal saldoIni  = parseBigDecimal(req.getParameter("saldoInicial"), BigDecimal.ZERO);

        service.cadastrar(numero, titular, tipo, saldoIni);
        req.getSession().setAttribute("sucesso", "Conta cadastrada com sucesso!");
        resp.sendRedirect(req.getContextPath() + "/contas/");
    }

    private void atualizar(HttpServletRequest req, HttpServletResponse resp, String pathInfo)
            throws Exception {
        Long id       = parseLong(pathInfo.substring("/editar/".length()));
        String titular = req.getParameter("titular");
        String tipo    = req.getParameter("tipo");
        boolean ativa  = "true".equals(req.getParameter("ativa"));

        service.editar(id, titular, tipo, ativa);
        req.getSession().setAttribute("sucesso", "Conta atualizada com sucesso!");
        resp.sendRedirect(req.getContextPath() + "/contas/");
    }

    private void populaFormEdicao(HttpServletRequest req) {
        // Repopula campos do formulário após erro de validação
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isBlank()) {
            Conta c = new Conta();
            try { c.setId(Long.parseLong(idParam)); } catch (NumberFormatException ignored) {}
            c.setNumero(req.getParameter("numero"));
            c.setTitular(req.getParameter("titular"));
            try { c.setTipo(Conta.Tipo.valueOf(req.getParameter("tipo"))); } catch (Exception ignored) {}
            req.setAttribute("conta", c);
        }
    }

    // ── Parsers ───────────────────────────────────────────────

    private Long parseLong(String s) throws RegraNegocioException {
        try { return Long.parseLong(s.trim()); }
        catch (Exception e) { throw new RegraNegocioException("Identificador inválido: " + s); }
    }

    private BigDecimal parseBigDecimal(String s, BigDecimal fallback) {
        try { return new BigDecimal(s.replace(",", ".").trim()); }
        catch (Exception e) { return fallback; }
    }
}
