package br.com.financeiro.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Inicializa e encerra recursos da aplicação junto ao ciclo de vida do contexto Servlet.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Força a inicialização do pool de conexões na subida da aplicação
        ConnectionFactory.getDataSource();
        sce.getServletContext().log("Pool de conexões JDBC inicializado.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionFactory.shutdown();
        sce.getServletContext().log("Pool de conexões JDBC encerrado.");
    }
}
