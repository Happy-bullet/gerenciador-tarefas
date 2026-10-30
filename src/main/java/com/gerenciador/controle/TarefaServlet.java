package com.gerenciador.controle;

import com.gerenciador.dao.TarefaDAO;
import com.gerenciador.erro.PersistenciaException;
import com.gerenciador.erro.ValidacaoException;
import com.gerenciador.modelo.Andamento;
import com.gerenciador.modelo.Prioridade;
import com.gerenciador.modelo.Tarefa;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;

/**
 * Controller (o "C" do MVC) das tarefas
 * GET  /tarefas                       -> busca as tarefas no DAO e encaminha para listarTarefas.jsp
 * GET  /tarefas?acao=editar&id=N      -> carrega a tarefa N e abre cadastroTarefas.jsp em modo edição
 * POST /tarefas                       -> valida o formulário e grava uma nova tarefa
 * POST /tarefas (com campo id)        -> valida o formulário e atualiza a tarefa existente
 * POST /tarefas?acao=excluir (com id) -> exclui a tarefa
 */
@WebServlet("/tarefas")
public class TarefaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_MAXIMO_NOME = 255;

    private final transient TarefaDAO tarefaDAO = new TarefaDAO();

    /**
     * Lista as tarefas cadastradas ou, com acao=editar, abre o formulário de edição
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("editar".equals(request.getParameter("acao"))) {
            abrirEdicao(request, response);
            return;
        }
        listar(request, response);
    }

    /**
     * Recebe o formulário de cadastro/edição ou o pedido de exclusão
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Sem isso, acentos digitados no formulário chegam corrompidos
        request.setCharacterEncoding("UTF-8");

        if ("excluir".equals(request.getParameter("acao"))) {
            excluir(request, response);
            return;
        }

        // Campo oculto "id" preenchido = edição; vazio = cadastro de uma nova tarefa
        boolean edicao = request.getParameter("id") != null && !request.getParameter("id").trim().isEmpty();

        try {
            Tarefa tarefa = lerTarefa(request);

            // Post/Redirect/Get: redireciona para a listagem, assim um F5 não grava a tarefa de novo
            if (edicao) {
                Integer id = lerId(request);
                if (!tarefaDAO.atualizar(id, tarefa)) {
                    throw new ValidacaoException("A tarefa " + id + " não existe mais.");
                }
                response.sendRedirect(request.getContextPath() + "/tarefas?atualizada=" + id);
            } else {
                tarefaDAO.salvar(tarefa);
                response.sendRedirect(request.getContextPath() + "/tarefas?sucesso=" + tarefa.getId());
            }
        } catch (ValidacaoException e) {
            voltarParaCadastro(request, response, e.getMessage());
        } catch (PersistenciaException e) {
            e.printStackTrace();
            voltarParaCadastro(request, response, e.getMessage() + " Verifique se o MySQL está ligado.");
        }
    }

    /**
     * Consulta as tarefas e encaminha para a listagem
     */
    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("tarefas", tarefaDAO.listar());
        } catch (PersistenciaException e) {
            e.printStackTrace();
            request.setAttribute("tarefas", Collections.emptyList());
            request.setAttribute("erro", e.getMessage() + " Verifique se o MySQL está ligado.");
        }
        request.getRequestDispatcher("/listarTarefas.jsp").forward(request, response);
    }

    /**
     * Carrega a tarefa pelo id e abre o formulário já preenchido.
     * Se o id for inválido ou a tarefa não existir, volta para a listagem com a mensagem de erro
     */
    private void abrirEdicao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Integer id = lerId(request);
            Tarefa tarefa = tarefaDAO.buscarPorId(id);
            if (tarefa == null) {
                throw new ValidacaoException("A tarefa " + id + " não existe.");
            }
            request.setAttribute("tarefa", tarefa);
            request.getRequestDispatcher("/cadastroTarefas.jsp").forward(request, response);
        } catch (ValidacaoException e) {
            request.setAttribute("erro", e.getMessage());
            listar(request, response);
        } catch (PersistenciaException e) {
            e.printStackTrace();
            request.setAttribute("erro", e.getMessage() + " Verifique se o MySQL está ligado.");
            listar(request, response);
        }
    }

    /**
     * Exclui a tarefa e redireciona para a listagem
     */
    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Integer id = lerId(request);
            if (!tarefaDAO.excluir(id)) {
                throw new ValidacaoException("A tarefa " + id + " não existe mais.");
            }
            response.sendRedirect(request.getContextPath() + "/tarefas?excluida=" + id);
        } catch (ValidacaoException e) {
            request.setAttribute("erro", e.getMessage());
            listar(request, response);
        } catch (PersistenciaException e) {
            e.printStackTrace();
            request.setAttribute("erro", e.getMessage() + " Verifique se o MySQL está ligado.");
            listar(request, response);
        }
    }

    /**
     * Devolve o usuário ao formulário mostrando a mensagem de erro.
     * Os valores digitados continuam nos campos (o JSP lê de ${param})
     */
    private void voltarParaCadastro(HttpServletRequest request, HttpServletResponse response, String mensagem)
            throws ServletException, IOException {
        request.setAttribute("erro", mensagem);
        request.getRequestDispatcher("/cadastroTarefas.jsp").forward(request, response);
    }

    /**
     * Lê e valida os campos do formulário
     * @param request Requisição com os parâmetros nome, dataVencimento, prioridade e andamento
     * @return Tarefa pronta para ser gravada
     * @throws ValidacaoException se algum campo estiver vazio ou inválido
     */
    private Tarefa lerTarefa(HttpServletRequest request) throws ValidacaoException {
        String nome = lerTexto(request, "nome", "Nome da tarefa");
        if (nome.length() > TAMANHO_MAXIMO_NOME) {
            throw new ValidacaoException("O nome da tarefa pode ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.");
        }

        LocalDate dataVencimento;
        try {
            // O <input type="date"> sempre envia no formato aaaa-mm-dd, o mesmo do tipo DATE do MySQL
            dataVencimento = LocalDate.parse(lerTexto(request, "dataVencimento", "Data de vencimento"));
        } catch (DateTimeParseException e) {
            throw new ValidacaoException("Data de vencimento inválida.");
        }

        Prioridade prioridade;
        Andamento andamento;
        try {
            prioridade = Prioridade.deValor(lerTexto(request, "prioridade", "Prioridade"));
            andamento = Andamento.deValor(lerTexto(request, "andamento", "Andamento"));
        } catch (IllegalArgumentException e) {
            throw new ValidacaoException(e.getMessage());
        }

        return new Tarefa(nome, dataVencimento, prioridade, andamento);
    }

    /**
     * Lê o parâmetro "id" da requisição
     * @param request Requisição
     * @return Id como número inteiro
     * @throws ValidacaoException se o id estiver vazio ou não for um número
     */
    private Integer lerId(HttpServletRequest request) throws ValidacaoException {
        String valor = lerTexto(request, "id", "ID");
        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException e) {
            throw new ValidacaoException("ID de tarefa inválido.");
        }
    }

    /**
     * Lê um parâmetro de texto obrigatório
     * @param request Requisição
     * @param parametro Nome do campo no formulário
     * @param descricao Nome do campo usado na mensagem de erro
     * @return Valor sem espaços nas pontas
     * @throws ValidacaoException se o campo vier vazio
     */
    private String lerTexto(HttpServletRequest request, String parametro, String descricao) throws ValidacaoException {
        String valor = request.getParameter(parametro);
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacaoException("O campo " + descricao + " é obrigatório.");
        }
        return valor.trim();
    }
}
