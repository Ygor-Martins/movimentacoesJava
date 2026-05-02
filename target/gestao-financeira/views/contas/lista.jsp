<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/layout.jsp"><jsp:param name="titulo" value="Contas"/></jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Contas</h1>
        <p class="page-subtitle">${contas.size()} conta(s) cadastrada(s)</p>
    </div>
    <a href="${pageContext.request.contextPath}/contas/nova" class="btn btn-primary">+ Nova Conta</a>
</div>

<div class="card">
    <div class="table-wrap">
        <c:choose>
            <c:when test="${empty contas}">
                <div class="empty-state">
                    <div class="empty-icon">◈</div>
                    <p>Nenhuma conta cadastrada ainda.</p>
                    <a href="${pageContext.request.contextPath}/contas/nova" class="btn btn-primary" style="margin-top:1rem">Criar primeira conta</a>
                </div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>#</th><th>Número</th><th>Titular</th><th>Tipo</th>
                            <th style="text-align:right">Saldo</th>
                            <th>Status</th><th>Criada em</th><th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${contas}">
                        <tr>
                            <td style="color:var(--text-dim);font-size:0.78rem;">${c.id}</td>
                            <td class="mono">${c.numero}</td>
                            <td>${c.titular}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${c.tipo == 'CORRENTE'}"><span class="badge badge-blue">Corrente</span></c:when>
                                    <c:when test="${c.tipo == 'POUPANCA'}"><span class="badge badge-green">Poupança</span></c:when>
                                    <c:otherwise><span class="badge badge-gold">Investimento</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td style="text-align:right">
                                <span class="valor">R$ <fmt:formatNumber value="${c.saldo}" minFractionDigits="2" maxFractionDigits="2"/></span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${c.ativa}"><span class="badge badge-green">● Ativa</span></c:when>
                                    <c:otherwise><span class="badge badge-gray">○ Inativa</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td style="color:var(--text-muted);font-size:0.8rem;">
                                <fmt:formatDate value="${c.criadaEm}" pattern="dd/MM/yyyy"/>
                            </td>
                            <td>
                                <div style="display:flex;gap:.5rem;">
                                    <a href="${pageContext.request.contextPath}/contas/editar/${c.id}" class="btn btn-ghost btn-sm">Editar</a>
                                    <a href="${pageContext.request.contextPath}/movimentacoes/extrato?contaId=${c.id}" class="btn btn-ghost btn-sm">Extrato</a>
                                </div>
                            </td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

</main></body></html>
