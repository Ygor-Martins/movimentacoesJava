<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="editando" value="${not empty conta and not empty conta.id}"/>
<jsp:include page="/WEB-INF/layout.jsp">
    <jsp:param name="titulo" value="${editando ? 'Editar Conta' : 'Nova Conta'}"/>
</jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">${editando ? 'Editar Conta' : 'Nova Conta'}</h1>
        <p class="page-subtitle">${editando ? 'Altere os dados da conta' : 'Preencha os dados para cadastrar'}</p>
    </div>
    <a href="${pageContext.request.contextPath}/contas/" class="btn btn-ghost">← Voltar</a>
</div>

<div class="card" style="max-width:640px;">
    <c:choose>
        <%-- FORMULÁRIO DE EDIÇÃO --%>
        <c:when test="${editando}">
            <form action="${pageContext.request.contextPath}/contas/editar/${conta.id}" method="post">
                <input type="hidden" name="id" value="${conta.id}">
                <div class="form-grid">
                    <div class="form-group">
                        <label>Número da Conta</label>
                        <input type="text" value="${conta.numero}" disabled>
                        <span class="form-hint">O número não pode ser alterado.</span>
                    </div>
                    <div class="form-group">
                        <label for="tipo">Tipo</label>
                        <select id="tipo" name="tipo" required>
                            <option value="CORRENTE"     ${conta.tipo == 'CORRENTE'     ? 'selected' : ''}>Corrente</option>
                            <option value="POUPANCA"     ${conta.tipo == 'POUPANCA'     ? 'selected' : ''}>Poupança</option>
                            <option value="INVESTIMENTO" ${conta.tipo == 'INVESTIMENTO' ? 'selected' : ''}>Investimento</option>
                        </select>
                    </div>
                    <div class="form-group full">
                        <label for="titular">Titular</label>
                        <input type="text" id="titular" name="titular" value="${conta.titular}" required maxlength="120">
                    </div>
                    <div class="form-group full">
                        <label>Status da Conta</label>
                        <div class="toggle-group">
                            <label class="toggle">
                                <input type="checkbox" name="ativa" value="true" ${conta.ativa ? 'checked' : ''}>
                                <span class="toggle-slider"></span>
                            </label>
                            <span style="font-size:.875rem;">Conta ativa</span>
                        </div>
                        <span class="form-hint">Contas com saldo não podem ser desativadas.</span>
                    </div>
                </div>
                <div class="divider"></div>
                <div style="display:flex;gap:.75rem;justify-content:flex-end;">
                    <a href="${pageContext.request.contextPath}/contas/" class="btn btn-ghost">Cancelar</a>
                    <button type="submit" class="btn btn-primary">Salvar Alterações</button>
                </div>
            </form>
        </c:when>

        <%-- FORMULÁRIO DE CADASTRO --%>
        <c:otherwise>
            <form action="${pageContext.request.contextPath}/contas/salvar" method="post">
                <div class="form-grid">
                    <div class="form-group">
                        <label for="numero">Número da Conta</label>
                        <input type="text" id="numero" name="numero" placeholder="ex: 0005-9"
                               required maxlength="20" value="${param.numero}">
                    </div>
                    <div class="form-group">
                        <label for="tipo">Tipo</label>
                        <select id="tipo" name="tipo" required>
                            <option value="" disabled selected>Selecione…</option>
                            <option value="CORRENTE">Corrente</option>
                            <option value="POUPANCA">Poupança</option>
                            <option value="INVESTIMENTO">Investimento</option>
                        </select>
                    </div>
                    <div class="form-group full">
                        <label for="titular">Titular</label>
                        <input type="text" id="titular" name="titular" placeholder="Nome completo"
                               required maxlength="120" value="${param.titular}">
                    </div>
                    <div class="form-group full">
                        <label for="saldoInicial">Saldo Inicial (R$)</label>
                        <input type="number" id="saldoInicial" name="saldoInicial"
                               placeholder="0,00" min="0" step="0.01" value="0">
                        <span class="form-hint">Pode ser zero. Não pode ser negativo.</span>
                    </div>
                </div>
                <div class="divider"></div>
                <div style="display:flex;gap:.75rem;justify-content:flex-end;">
                    <a href="${pageContext.request.contextPath}/contas/" class="btn btn-ghost">Cancelar</a>
                    <button type="submit" class="btn btn-primary">Cadastrar Conta</button>
                </div>
            </form>
        </c:otherwise>
    </c:choose>
</div>

</main></body></html>
