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
 * GET  /tarefas -> busca as tarefas no DAO e encaminha para listarTarefas.jsp
 * POST /tarefas -> valida o formulário de cadastroTarefas.jsp e grava a nova tarefa
 */
@WebServlet("/tarefas")
public class TarefaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_MAXIMO_NOME = 255;

    private final transient TarefaDAO tarefaDAO = new TarefaDAO();

    /**
     * Lista as tarefas cadastradas
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
     * Recebe o formulário de cadastro, valida e grava a tarefa
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Sem isso, acentos digitados no formulário chegam corrompidos
        request.setCharacterEncoding("UTF-8");

        try {
            Tarefa tarefa = lerTarefa(request);
            tarefaDAO.salvar(tarefa);

            // Post/Redirect/Get: redireciona para a listagem, assim um F5 não cadastra a tarefa de novo
            response.sendRedirect(request.getContextPath() + "/tarefas?sucesso=" + tarefa.getId());
        } catch (ValidacaoException e) {
            voltarParaCadastro(request, response, e.getMessage());
        } catch (PersistenciaException e) {
            e.printStackTrace();
            voltarParaCadastro(request, response, e.getMessage() + " Verifique se o MySQL está ligado.");
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
