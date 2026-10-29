<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
    View de cadastro (o "V" do MVC).
    O formulário envia um POST para /tarefas, tratado pelo TarefaServlet.
    Se a validação falhar, o servlet volta para esta página com ${erro} preenchido
    e os valores digitados continuam disponíveis em ${param}.
--%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cadastro de Tarefas</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container py-4" style="max-width: 560px;">

    <h1 class="h3 mb-4">Cadastro de Tarefas</h1>

    <c:if test="${not empty erro}">
        <div class="alert alert-danger">${fn:escapeXml(erro)}</div>
    </c:if>

    <%-- Valores padrão dos selects: normal / pendente, como no exemplo do enunciado --%>
    <c:set var="prioridadeEscolhida" value="${empty param.prioridade ? 'normal' : param.prioridade}"/>
    <c:set var="andamentoEscolhido" value="${empty param.andamento ? 'pendente' : param.andamento}"/>

    <form action="${pageContext.request.contextPath}/tarefas" method="post">

        <div class="mb-3">
            <label for="nome" class="form-label">Nome da Tarefa</label>
            <input type="text" class="form-control" id="nome" name="nome" maxlength="255" required
                   placeholder="Digite o nome da tarefa" value="${fn:escapeXml(param.nome)}">
        </div>

        <div class="mb-3">
            <label for="dataVencimento" class="form-label">Data de Vencimento</label>
            <input type="date" class="form-control" id="dataVencimento" name="dataVencimento" required
                   value="${fn:escapeXml(param.dataVencimento)}">
        </div>

        <%-- Os "value" das opções são exatamente os valores do ENUM no banco --%>
        <div class="mb-3">
            <label for="prioridade" class="form-label">Prioridade</label>
            <select class="form-select" id="prioridade" name="prioridade" required>
                <option value="normal" ${prioridadeEscolhida == 'normal' ? 'selected' : ''}>Normal</option>
                <option value="alta" ${prioridadeEscolhida == 'alta' ? 'selected' : ''}>Alta</option>
                <option value="baixa" ${prioridadeEscolhida == 'baixa' ? 'selected' : ''}>Baixa</option>
            </select>
        </div>

        <div class="mb-4">
            <label for="andamento" class="form-label">Andamento</label>
            <select class="form-select" id="andamento" name="andamento" required>
                <option value="pendente" ${andamentoEscolhido == 'pendente' ? 'selected' : ''}>Pendente</option>
                <option value="em progresso" ${andamentoEscolhido == 'em progresso' ? 'selected' : ''}>Em Progresso</option>
                <option value="pronto" ${andamentoEscolhido == 'pronto' ? 'selected' : ''}>Pronto</option>
            </select>
        </div>

        <button type="submit" class="btn btn-primary">Adicionar Tarefa</button>
        <a href="${pageContext.request.contextPath}/tarefas" class="btn btn-link">Ver lista de tarefas</a>
    </form>

</div>
</body>
</html>
