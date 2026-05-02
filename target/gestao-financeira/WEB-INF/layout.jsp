<%-- /WEB-INF/layout.jsp  –  incluído via jsp:include --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.titulo != null ? param.titulo.concat(' · ') : ''}FinanceCore</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:wght@300;400;500;600&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css">
</head>
<body>

<nav class="navbar">
    <a class="brand" href="${pageContext.request.contextPath}/">
        <span class="brand-icon">◈</span>
        <span>FinanceCore</span>
    </a>
    <ul class="nav-links">
        <li><a href="${pageContext.request.contextPath}/"             class="nav-link">Painel</a></li>
        <li><a href="${pageContext.request.contextPath}/contas/"      class="nav-link">Contas</a></li>
        <li><a href="${pageContext.request.contextPath}/transferencias/" class="nav-link">Transferir</a></li>
        <li><a href="${pageContext.request.contextPath}/movimentacoes/" class="nav-link">Movimentações</a></li>
    </ul>
</nav>

<main class="main-content">

<%-- Flash messages --%>
<c:if test="${not empty sessionScope.sucesso}">
    <div class="alert alert-success">
        <span class="alert-icon">✓</span>
        ${sessionScope.sucesso}
        <c:remove var="sucesso" scope="session"/>
    </div>
</c:if>
<c:if test="${not empty requestScope.erro}">
    <div class="alert alert-error">
        <span class="alert-icon">✕</span>
        ${requestScope.erro}
    </div>
</c:if>
