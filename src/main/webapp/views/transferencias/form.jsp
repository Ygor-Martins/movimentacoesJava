<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/WEB-INF/layout.jsp"><jsp:param name="titulo" value="Transferência"/></jsp:include>

<div class="page-header">
    <div>
        <h1 class="page-title">Transferência</h1>
        <p class="page-subtitle">Mova saldo entre contas internas</p>
    </div>
    <a href="${pageContext.request.contextPath}/" class="btn btn-ghost">← Painel</a>
</div>

<div style="display:grid;grid-template-columns:1fr 340px;gap:1.5rem;align-items:start;">

    <%-- Formulário Principal --%>
    <div class="card">
        <form action="${pageContext.request.contextPath}/transferencias/" method="post" id="frmTransf">

            <div class="form-group" style="margin-bottom:1.25rem;">
                <label for="origemId">Conta de Origem</label>
                <select id="origemId" name="origemId" required onchange="atualizarSaldo(this,'saldoOrigem')">
                    <option value="" disabled selected>Selecione a conta de origem…</option>
                    <c:forEach var="c" items="${contas}">
                        <option value="${c.id}"
                                data-saldo="<fmt:formatNumber value='${c.saldo}' minFractionDigits='2' maxFractionDigits='2'/>"
                                ${origemSel == c.id ? 'selected' : ''}>
                            ${c.numero} — ${c.titular}
                        </option>
                    </c:forEach>
                </select>
                <span id="saldoOrigem" class="form-hint"></span>
            </div>

            <div class="form-group" style="margin-bottom:1.25rem;">
                <label for="destinoId">Conta de Destino</label>
                <select id="destinoId" name="destinoId" required onchange="atualizarSaldo(this,'saldoDestino')">
                    <option value="" disabled selected>Selecione a conta de destino…</option>
                    <c:forEach var="c" items="${contas}">
                        <option value="${c.id}"
                                data-saldo="<fmt:formatNumber value='${c.saldo}' minFractionDigits='2' maxFractionDigits='2'/>"
                                ${destinoSel == c.id ? 'selected' : ''}>
                            ${c.numero} — ${c.titular}
                        </option>
                    </c:forEach>
                </select>
                <span id="saldoDestino" class="form-hint"></span>
            </div>

            <div class="form-grid" style="margin-bottom:1.25rem;">
                <div class="form-group">
                    <label for="valor">Valor (R$)</label>
                    <input type="number" id="valor" name="valor"
                           placeholder="0,00" min="0.01" step="0.01"
                           value="${valorStr}" required>
                </div>
                <div class="form-group">
                    <label for="descricao">Descrição (opcional)</label>
                    <input type="text" id="descricao" name="descricao"
                           placeholder="Motivo da transferência" maxlength="255"
                           value="${descricao}">
                </div>
            </div>

            <div class="divider"></div>
            <div style="display:flex;gap:.75rem;justify-content:flex-end;">
                <a href="${pageContext.request.contextPath}/" class="btn btn-ghost">Cancelar</a>
                <button type="submit" class="btn btn-primary" onclick="return confirmar()">
                    ⇄ Executar Transferência
                </button>
            </div>
        </form>
    </div>

    <%-- Painel informativo --%>
    <div>
        <div class="card card-sm" style="margin-bottom:1rem;border-color:rgba(200,169,110,0.3);">
            <div style="font-size:.72rem;font-weight:600;letter-spacing:.08em;text-transform:uppercase;color:var(--accent);margin-bottom:.75rem;">Como funciona</div>
            <ul style="list-style:none;display:flex;flex-direction:column;gap:.6rem;">
                <li style="display:flex;gap:.6rem;font-size:.83rem;color:var(--text-muted);">
                    <span style="color:var(--accent);">01</span>
                    Selecione a conta origem (será debitada).
                </li>
                <li style="display:flex;gap:.6rem;font-size:.83rem;color:var(--text-muted);">
                    <span style="color:var(--accent);">02</span>
                    Selecione a conta destino (será creditada).
                </li>
                <li style="display:flex;gap:.6rem;font-size:.83rem;color:var(--text-muted);">
                    <span style="color:var(--accent);">03</span>
                    Informe o valor. Deve ser maior que zero e não exceder o saldo disponível.
                </li>
                <li style="display:flex;gap:.6rem;font-size:.83rem;color:var(--text-muted);">
                    <span style="color:var(--accent);">04</span>
                    A operação é atômica: débito e crédito ocorrem na mesma transação.
                </li>
            </ul>
        </div>
        <div class="card card-sm" style="background:var(--red-dim);border-color:rgba(248,113,113,0.2);">
            <div style="font-size:.72rem;font-weight:600;letter-spacing:.08em;text-transform:uppercase;color:var(--red);margin-bottom:.5rem;">Atenção</div>
            <p style="font-size:.83rem;color:var(--text-muted);">Transferências não podem ser desfeitas após a confirmação.</p>
        </div>
    </div>
</div>

<script>
function atualizarSaldo(sel, spanId) {
    const opt = sel.options[sel.selectedIndex];
    const saldo = opt ? opt.getAttribute('data-saldo') : null;
    const span = document.getElementById(spanId);
    span.textContent = saldo ? 'Saldo atual: R$ ' + saldo : '';
    span.style.color = saldo ? 'var(--accent)' : '';
}
function confirmar() {
    const v = parseFloat(document.getElementById('valor').value);
    if (isNaN(v) || v <= 0) { alert('Informe um valor válido.'); return false; }
    const orig = document.getElementById('origemId').value;
    const dest = document.getElementById('destinoId').value;
    if (orig === dest) { alert('Origem e destino não podem ser a mesma conta.'); return false; }
    return confirm('Confirmar transferência de R$ ' + v.toFixed(2) + '?');
}
// Inicializa saldos se houver seleção prévia (retorno de erro)
window.onload = function() {
    const o = document.getElementById('origemId');
    const d = document.getElementById('destinoId');
    if (o.value) atualizarSaldo(o, 'saldoOrigem');
    if (d.value) atualizarSaldo(d, 'saldoDestino');
};
</script>

</main></body></html>
