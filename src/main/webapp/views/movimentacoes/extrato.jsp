<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/layout.jsp"><jsp:param name="titulo" value="Extrato"/></jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Extrato</h1>
        <c:if test="${not empty conta}">
            <p class="page-subtitle">${conta.numero} · ${conta.titular}</p>
        </c:if>
    </div>
</div>

<%-- Seletor de Conta --%>
<div class="card" style="margin-bottom:1.5rem;">
    <form action="${pageContext.request.contextPath}/movimentacoes/extrato" method="get"
          style="display:flex;gap:1rem;align-items:flex-end;flex-wrap:wrap;">
        <div class="form-group" style="flex:1;min-width:220px;">
            <label for="contaId">Selecionar Conta</label>
            <select id="contaId" name="contaId">
                <option value="" disabled ${empty contaIdSel ? 'selected' : ''}>Escolha uma conta…</option>
                <c:forEach var="c" items="${contas}">
                    <option value="${c.id}" ${contaIdSel == c.id ? 'selected' : ''}>
                        ${c.numero} — ${c.titular}
                    </option>
                </c:forEach>
            </select>
        </div>
        <button type="submit" class="btn btn-primary" style="margin-bottom:0;">Ver Extrato</button>
    </form>
</div>

<c:if test="${not empty conta}">
    <%-- Resumo da Conta --%>
    <div class="stat-grid" style="margin-bottom:1.5rem;">
        <div class="stat-card">
            <div class="stat-label">Saldo Atual</div>
            <div class="stat-value gold">
                R$ <fmt:formatNumber value="${conta.saldo}" minFractionDigits="2" maxFractionDigits="2"/>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Tipo</div>
            <div class="stat-value" style="font-size:1.2rem;">${conta.tipo}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Status</div>
            <div class="stat-value" style="font-size:1.2rem;">
                ${conta.ativa ? 'Ativa' : 'Inativa'}
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Movimentações</div>
            <div class="stat-value">${movimentacoes.size()}</div>
        </div>
    </div>

    <%-- Tabela de Movimentações --%>
    <div class="card">
        <h2 style="font-family:var(--font-serif);font-size:1.1rem;margin-bottom:1.25rem;">Histórico de Movimentações</h2>
        <c:choose>
            <c:when test="${empty movimentacoes}">
                <div class="empty-state">
                    <div class="empty-icon">◈</div>
                    <p>Nenhuma movimentação registrada para esta conta.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>Data/Hora</th><th>Tipo</th><th>Descrição</th>
                                <th>Origem</th><th>Destino</th><th style="text-align:right">Valor</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="m" items="${movimentacoes}">
                            <tr>
                                <td class="mono" style="font-size:.78rem;color:var(--text-muted);white-space:nowrap;">
                                    <fmt:formatDate value="${m.realizadaEm}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${m.tipo == 'CREDITO'}"><span class="badge badge-green">Crédito</span></c:when>
                                        <c:when test="${m.tipo == 'DEBITO'}"><span class="badge badge-red">Débito</span></c:when>
                                        <c:when test="${m.tipo == 'TRANSFERENCIA_ENTRADA'}"><span class="badge badge-blue">Transf. Recebida</span></c:when>
                                        <c:otherwise><span class="badge badge-gold">Transf. Enviada</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="color:var(--text-muted);font-size:.85rem;">
                                    <c:out value="${m.descricao}" default="—"/>
                                </td>
                                <td class="mono" style="font-size:.78rem;">
                                    <c:out value="${m.numeroContaOrigem}" default="—"/>
                                </td>
                                <td class="mono" style="font-size:.78rem;">
                                    <c:out value="${m.numeroContaDestino}" default="—"/>
                                </td>
                                <td style="text-align:right;white-space:nowrap;">
                                    <c:choose>
                                        <c:when test="${m.tipo == 'CREDITO' or m.tipo == 'TRANSFERENCIA_ENTRADA'}">
                                            <span class="valor valor-positivo">
                                                + R$ <fmt:formatNumber value="${m.valor}" minFractionDigits="2" maxFractionDigits="2"/>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="valor valor-negativo">
                                                − R$ <fmt:formatNumber value="${m.valor}" minFractionDigits="2" maxFractionDigits="2"/>
                                            </span>
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
</c:if>

</main></body></html>
