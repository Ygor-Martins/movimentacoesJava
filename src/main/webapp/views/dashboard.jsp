<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/layout.jsp"><jsp:param name="titulo" value="Painel"/></jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Painel Geral</h1>
        <p class="page-subtitle">Visão consolidada das contas e últimas movimentações</p>
    </div>
    <div style="display:flex;gap:.75rem;">
        <a href="${pageContext.request.contextPath}/contas/nova" class="btn btn-ghost btn-sm">+ Nova Conta</a>
        <a href="${pageContext.request.contextPath}/transferencias/" class="btn btn-primary btn-sm">⇄ Transferir</a>
    </div>
</div>

<%-- Stats --%>
<div class="stat-grid">
    <div class="stat-card">
        <div class="stat-label">Saldo Consolidado</div>
        <div class="stat-value gold">
            <fmt:formatNumber value="${totalSaldo}" type="currency" currencySymbol="R$" minFractionDigits="2"/>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-label">Contas Ativas</div>
        <div class="stat-value">${totalAtivas}</div>
    </div>
    <div class="stat-card">
        <div class="stat-label">Total de Contas</div>
        <div class="stat-value">${contas.size()}</div>
    </div>
</div>

<%-- Tabela de Contas --%>
<div class="card" style="margin-bottom:2rem;">
    <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:1.25rem;">
        <h2 style="font-family:var(--font-serif);font-size:1.2rem;">Contas Cadastradas</h2>
        <a href="${pageContext.request.contextPath}/contas/" class="btn btn-ghost btn-sm">Ver todas →</a>
    </div>
    <div class="table-wrap">
        <c:choose>
            <c:when test="${empty contas}">
                <div class="empty-state"><div class="empty-icon">◈</div><p>Nenhuma conta cadastrada.</p></div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>Número</th><th>Titular</th><th>Tipo</th>
                            <th style="text-align:right">Saldo</th><th>Status</th><th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${contas}">
                        <tr>
                            <td class="mono">${c.numero}</td>
                            <td>${c.titular}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${c.tipo == 'CORRENTE'}">
                                        <span class="badge badge-blue">Corrente</span>
                                    </c:when>
                                    <c:when test="${c.tipo == 'POUPANCA'}">
                                        <span class="badge badge-green">Poupança</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-gold">Investimento</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td style="text-align:right">
                                <span class="valor">
                                    <fmt:formatNumber value="${c.saldo}" type="currency" currencySymbol="R$" minFractionDigits="2"/>
                                </span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${c.ativa}"><span class="badge badge-green">Ativa</span></c:when>
                                    <c:otherwise><span class="badge badge-gray">Inativa</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/movimentacoes/extrato?contaId=${c.id}" class="btn btn-ghost btn-sm">Extrato</a>
                            </td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%-- Últimas movimentações --%>
<div class="card">
    <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:1.25rem;">
        <h2 style="font-family:var(--font-serif);font-size:1.2rem;">Últimas Movimentações</h2>
        <a href="${pageContext.request.contextPath}/movimentacoes/" class="btn btn-ghost btn-sm">Ver todas →</a>
    </div>
    <c:choose>
        <c:when test="${empty recentes}">
            <div class="empty-state"><div class="empty-icon">◈</div><p>Nenhuma movimentação registrada.</p></div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>Data</th><th>Tipo</th><th>Conta</th><th>Descrição</th><th style="text-align:right">Valor</th></tr></thead>
                    <tbody>
                        <c:forEach var="m" items="${recentes}">
                        <tr>
                            <td class="mono" style="font-size:0.78rem;color:var(--text-muted)">
                                <fmt:formatDate value="${m.realizadaEm}" pattern="dd/MM/yy HH:mm" type="both"/>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${m.tipo == 'CREDITO'}"><span class="badge badge-green">Crédito</span></c:when>
                                    <c:when test="${m.tipo == 'DEBITO'}"><span class="badge badge-red">Débito</span></c:when>
                                    <c:when test="${m.tipo == 'TRANSFERENCIA_ENTRADA'}"><span class="badge badge-blue">Entrada</span></c:when>
                                    <c:otherwise><span class="badge badge-gold">Saída</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="mono" style="font-size:0.8rem;">${m.contaId}</td>
                            <td style="color:var(--text-muted);font-size:0.85rem;">${m.descricao}</td>
                            <td style="text-align:right">
                                <c:choose>
                                    <c:when test="${m.tipo == 'CREDITO' or m.tipo == 'TRANSFERENCIA_ENTRADA'}">
                                        <span class="valor valor-positivo">+<fmt:formatNumber value="${m.valor}" minFractionDigits="2"/></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="valor valor-negativo">-<fmt:formatNumber value="${m.valor}" minFractionDigits="2"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

</main></body></html>
