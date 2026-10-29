package com.gerenciador.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Cria uma única SessionFactory para toda a aplicação.
 * A SessionFactory é cara de construir, por isso fica em um campo estático e é reaproveitada.
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Lê o arquivo src/main/resources/hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Falha na criação da SessionFactory." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Fecha a SessionFactory e libera as conexões com o banco.
     * Chamado pelo AplicacaoListener quando o Tomcat encerra a aplicação.
     */
    public static void fechar() {
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
        }
    }
}
