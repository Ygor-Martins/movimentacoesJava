<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<title>Erro interno · FinanceCore</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=DM+Serif+Display&family=DM+Sans:wght@400;500&display=swap" rel="stylesheet">
<style>
:root{--bg:#0d0f14;--text:#e8eaf2;--red:#f87171;--text-muted:#6b7494;--accent:#c8a96e;}
*{box-sizing:border-box;margin:0;padding:0;}
body{background:var(--bg);color:var(--text);font-family:'DM Sans',sans-serif;
     min-height:100vh;display:flex;align-items:center;justify-content:center;text-align:center;}
.code{font-family:'DM Serif Display',serif;font-size:6rem;color:var(--red);line-height:1;}
h1{font-family:'DM Serif Display',serif;font-size:1.5rem;margin:.5rem 0 1rem;}
p{color:var(--text-muted);margin-bottom:.5rem;}
a{display:inline-block;margin-top:1rem;padding:.6rem 1.5rem;background:var(--accent);color:#0d0f14;
  text-decoration:none;border-radius:6px;font-weight:600;}
</style>
</head>
<body>
<div>
  <div class="code">500</div>
  <h1>Erro interno do servidor</h1>
  <p>Ocorreu um erro inesperado ao processar sua requisição.</p>
  <p>Por favor, tente novamente ou contate o suporte.</p>
  <a href="${pageContext.request.contextPath}/">← Voltar ao Painel</a>
</div>
</body>
</html>
