<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
    View de cadastro e edição (o "V" do MVC).
    O formulário envia um POST para /tarefas, tratado pelo TarefaServlet.
    - Cadastro: campo oculto "id" vazio.
    - Edição: o servlet coloca a tarefa em ${tarefa} e o campo oculto "id" leva o id dela.
    Se a validação falhar, o servlet volta para esta página com ${erro} preenchido
    e os valores digitados continuam disponíveis em ${param}.
--%>
<%-- Na primeira abertura da edição os valores vêm de ${tarefa}; depois de um erro de validação, vêm de ${param} --%>
<c:choose>
    <c:when test="${not empty tarefa}">
        <c:set var="idTarefa" value="${tarefa.id}"/>
        <c:set var="nomeValor" value="${tarefa.nome}"/>
        <c:set var="dataValor" value="${tarefa.dataVencimento}"/>
        <c:set var="prioridadeEscolhida" value="${tarefa.prioridade.valor}"/>
        <c:set var="andamentoEscolhido" value="${tarefa.andamento.valor}"/>
    </c:when>
    <c:otherwise>
        <c:set var="idTarefa" value="${param.id}"/>
        <c:set var="nomeValor" value="${param.nome}"/>
        <c:set var="dataValor" value="${param.dataVencimento}"/>
        <%-- Valores padrão dos selects: normal / pendente, como no exemplo do enunciado --%>
        <c:set var="prioridadeEscolhida" value="${empty param.prioridade ? 'normal' : param.prioridade}"/>
        <c:set var="andamentoEscolhido" value="${empty param.andamento ? 'pendente' : param.andamento}"/>
    </c:otherwise>
</c:choose>
<c:set var="edicao" value="${not empty idTarefa}"/>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${edicao ? 'Editar Tarefa' : 'Cadastro de Tarefas'}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container py-4" style="max-width: 560px;">

    <h1 class="h3 mb-4">
        <c:choose>
            <c:when test="${edicao}">Editar Tarefa #${fn:escapeXml(idTarefa)}</c:when>
            <c:otherwise>Cadastro de Tarefas</c:otherwise>
        </c:choose>
    </h1>

    <c:if test="${not empty erro}">
        <div class="alert alert-danger">${fn:escapeXml(erro)}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/tarefas" method="post">

        <%-- Vazio no cadastro; com o id da tarefa na edição --%>
        <input type="hidden" name="id" value="${fn:escapeXml(idTarefa)}">

        <div class="mb-3">
            <label for="nome" class="form-label">Nome da Tarefa</label>
            <input type="text" class="form-control" id="nome" name="nome" maxlength="255" required
                   placeholder="Digite o nome da tarefa" value="${fn:escapeXml(nomeValor)}">
        </div>

        <div class="mb-3">
            <label for="dataVencimento" class="form-label">Data de Vencimento</label>
            <input type="date" class="form-control" id="dataVencimento" name="dataVencimento" required
                   value="${fn:escapeXml(dataValor)}">
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

        <c:choose>
            <c:when test="${edicao}">
                <button type="submit" class="btn btn-primary">Salvar Alterações</button>
                <a href="${pageContext.request.contextPath}/tarefas" class="btn btn-secondary">Cancelar</a>
            </c:when>
            <c:otherwise>
                <button type="submit" class="btn btn-primary">Adicionar Tarefa</button>
                <a href="${pageContext.request.contextPath}/tarefas" class="btn btn-link">Ver lista de tarefas</a>
            </c:otherwise>
        </c:choose>
    </form>

</div>
</body>
</html>
