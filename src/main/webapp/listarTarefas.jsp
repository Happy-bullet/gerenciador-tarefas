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

    <c:if test="${not empty param.atualizada}">
        <div class="alert alert-success">Tarefa ${fn:escapeXml(param.atualizada)} atualizada com sucesso!</div>
    </c:if>

    <c:if test="${not empty param.excluida}">
        <div class="alert alert-success">Tarefa ${fn:escapeXml(param.excluida)} excluída com sucesso!</div>
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
            <th class="text-center">Ações</th>
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
                <td class="text-center text-nowrap">
                    <%-- Editar: GET que abre o formulário já preenchido --%>
                    <a href="${pageContext.request.contextPath}/tarefas?acao=editar&id=${tarefa.id}"
                       class="btn btn-sm btn-outline-primary">Editar</a>

                    <%-- Excluir: POST (nunca GET, que pode ser disparado só por abrir um link), com confirmação --%>
                    <form action="${pageContext.request.contextPath}/tarefas" method="post" class="d-inline"
                          onsubmit="return confirm('Excluir a tarefa ${tarefa.id}?');">
                        <input type="hidden" name="acao" value="excluir">
                        <input type="hidden" name="id" value="${tarefa.id}">
                        <button type="submit" class="btn btn-sm btn-outline-danger">Excluir</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty tarefas}">
            <tr>
                <td colspan="6" class="text-center text-muted">Nenhuma tarefa cadastrada.</td>
            </tr>
        </c:if>
        </tbody>
    </table>

</div>
</body>
</html>
