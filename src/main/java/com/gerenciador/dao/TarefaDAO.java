package com.gerenciador.dao;

import com.gerenciador.erro.PersistenciaException;
import com.gerenciador.modelo.Tarefa;
import com.gerenciador.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Acesso aos dados da tabela Tarefas.
 * Nenhuma outra classe abre Session do Hibernate: tudo que envolve o banco passa por aqui.
 */
public class TarefaDAO {
    private static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    /**
     * Grava uma nova tarefa. Após o commit, o id gerado pelo AUTO_INCREMENT fica disponível em tarefa.getId()
     * @param tarefa Tarefa a ser gravada
     * @throws PersistenciaException se o banco recusar a gravação
     */
    public void salvar(Tarefa tarefa) throws PersistenciaException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(tarefa);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            throw new PersistenciaException("Não foi possível salvar a tarefa.", e);
        }
    }

    /**
     * Lista todas as tarefas cadastradas, em ordem de id
     * @return Lista de tarefas (vazia se não houver nenhuma)
     * @throws PersistenciaException se não for possível consultar o banco
     */
    public List<Tarefa> listar() throws PersistenciaException {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Tarefa order by id", Tarefa.class).list();
        } catch (Exception e) {
            throw new PersistenciaException("Não foi possível consultar as tarefas.", e);
        }
    }
}
