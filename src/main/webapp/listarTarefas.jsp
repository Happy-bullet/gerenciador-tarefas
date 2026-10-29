<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
    View de listagem (o "V" do MVC).
    Não acessa o banco: recebe a lista pronta em ${tarefas}, colocada pelo TarefaServlet.
    Se alguém abrir /listarTarefas.jsp direto, a lista não existe; então redireciona para /tarefas,
    que consulta o banco e volta para cá.
--%>
<c:if test="${tarefas == null}">
    <c:redirect url="/tarefas"/>
</c:if>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Lista de Tarefas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container py-4" style="max-width: 900px;">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3 mb-0">Lista de Tarefas</h1>
        <a href="${pageContext.request.contextPath}/cadastroTarefas.jsp" class="btn btn-primary">Nova Tarefa</a>
    </div>

    <c:if test="${not empty param.sucesso}">
        <div class="alert alert-success">Tarefa cadastrada com sucesso! ID: ${fn:escapeXml(param.sucesso)}</div>
    </c:if>

    <c:if test="${not empty erro}">
        <div class="alert alert-danger">${fn:escapeXml(erro)}</div>
    </c:if>

    <table class="table table-bordered table-hover align-middle">
        <thead class="table-light">
        <tr>
            <th>ID</th>
            <th>Nome da Tarefa</th>
            <th>Data de Vencimento</th>
            <th>Prioridade</th>
            <th>Andamento</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="tarefa" items="${tarefas}">
            <tr>
                <td>${tarefa.id}</td>
                <td>${fn:escapeXml(tarefa.nome)}</td>
                <td>${tarefa.dataVencimento}</td>
                <td>${tarefa.prioridade.rotulo}</td>
                <td>${tarefa.andamento.rotulo}</td>
            </tr>
        </c:forEach>
        <c:if test="${empty tarefas}">
            <tr>
                <td colspan="5" class="text-center text-muted">Nenhuma tarefa cadastrada.</td>
            </tr>
        </c:if>
        </tbody>
    </table>

</div>
</body>
</html>
