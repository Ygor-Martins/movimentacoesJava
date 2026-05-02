<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<title>Página não encontrada · FinanceCore</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=DM+Serif+Display&family=DM+Sans:wght@400;500&display=swap" rel="stylesheet">
<style>
:root{--bg:#0d0f14;--text:#e8eaf2;--accent:#c8a96e;--text-muted:#6b7494;}
*{box-sizing:border-box;margin:0;padding:0;}
body{background:var(--bg);color:var(--text);font-family:'DM Sans',sans-serif;
     min-height:100vh;display:flex;align-items:center;justify-content:center;text-align:center;}
.code{font-family:'DM Serif Display',serif;font-size:6rem;color:var(--accent);line-height:1;}
h1{font-family:'DM Serif Display',serif;font-size:1.5rem;margin:.5rem 0 1rem;}
p{color:var(--text-muted);margin-bottom:1.5rem;}
a{display:inline-block;padding:.6rem 1.5rem;background:var(--accent);color:#0d0f14;
  text-decoration:none;border-radius:6px;font-weight:600;}
</style>
</head>
<body>
<div>
  <div class="code">404</div>
  <h1>Página não encontrada</h1>
  <p>O recurso solicitado não existe.</p>
  <a href="${pageContext.request.contextPath}/">← Voltar ao Painel</a>
</div>
</body>
</html>
