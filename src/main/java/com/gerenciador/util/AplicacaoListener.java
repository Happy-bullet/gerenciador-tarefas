package com.gerenciador.util;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Escuta o ciclo de vida da aplicação no Tomcat.
 * Ao parar ou recarregar o projeto, fecha o Hibernate e a thread de limpeza do driver MySQL,
 * evitando o aviso de vazamento de memória que o Tomcat mostra no console.
 */
@WebListener
public class AplicacaoListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        HibernateUtil.fechar();
        AbandonedConnectionCleanupThread.checkedShutdown();
    }
}
