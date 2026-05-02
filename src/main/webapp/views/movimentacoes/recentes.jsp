<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/layout.jsp"><jsp:param name="titulo" value="Movimentações"/></jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Movimentações Recentes</h1>
        <p class="page-subtitle">Últimas 20 operações em todas as contas</p>
    </div>
    <a href="${pageContext.request.contextPath}/movimentacoes/extrato" class="btn btn-ghost">Extrato por Conta →</a>
</div>

<div class="card">
    <c:choose>
        <c:when test="${empty movimentacoes}">
            <div class="empty-state">
                <div class="empty-icon">◈</div>
                <p>Nenhuma movimentação registrada.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>Data/Hora</th><th>Tipo</th><th>Conta</th><th>Descrição</th>
                            <th>Origem</th><th>Destino</th><th style="text-align:right">Valor</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="m" items="${movimentacoes}">
                        <tr>
                            <td class="mono" style="font-size:.78rem;color:var(--text-muted);">
                                <fmt:formatDate value="${m.realizadaEm}" pattern="dd/MM/yyyy HH:mm"/>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${m.tipo == 'CREDITO'}"><span class="badge badge-green">Crédito</span></c:when>
                                    <c:when test="${m.tipo == 'DEBITO'}"><span class="badge badge-red">Débito</span></c:when>
                                    <c:when test="${m.tipo == 'TRANSFERENCIA_ENTRADA'}"><span class="badge badge-blue">T. Entrada</span></c:when>
                                    <c:otherwise><span class="badge badge-gold">T. Saída</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="mono" style="font-size:.8rem;">${m.contaId}</td>
                            <td style="color:var(--text-muted);font-size:.85rem;"><c:out value="${m.descricao}" default="—"/></td>
                            <td class="mono" style="font-size:.78rem;"><c:out value="${m.numeroContaOrigem}"  default="—"/></td>
                            <td class="mono" style="font-size:.78rem;"><c:out value="${m.numeroContaDestino}" default="—"/></td>
                            <td style="text-align:right;white-space:nowrap;">
                                <c:choose>
                                    <c:when test="${m.tipo == 'CREDITO' or m.tipo == 'TRANSFERENCIA_ENTRADA'}">
                                        <span class="valor valor-positivo">+<fmt:formatNumber value="${m.valor}" minFractionDigits="2"/></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="valor valor-negativo">−<fmt:formatNumber value="${m.valor}" minFractionDigits="2"/></span>
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
