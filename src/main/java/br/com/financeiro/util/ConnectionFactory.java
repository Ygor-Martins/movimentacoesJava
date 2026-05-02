package br.com.financeiro.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utilitário de conexão JDBC com pool HikariCP.
 * Configurações lidas de variáveis de ambiente (ou fallback para defaults de dev).
 */
public final class ConnectionFactory {

    private static final HikariDataSource DATA_SOURCE;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env("DB_URL",      "jdbc:postgresql://localhost:5432/financeiro"));
        config.setUsername(env("DB_USER",    "postgres"));
        config.setPassword(env("DB_PASS",    "postgres"));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30_000);
        config.setIdleTimeout(600_000);
        config.setMaxLifetime(1_800_000);
        config.setAutoCommit(false);               // controle explícito de transação
        config.addDataSourceProperty("cachePrepStmts",         "true");
        config.addDataSourceProperty("prepStmtCacheSize",      "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit",  "2048");

        DATA_SOURCE = new HikariDataSource(config);
    }

    private ConnectionFactory() {}

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    /** Encerra o pool ao desligar a aplicação. */
    public static void shutdown() {
        if (DATA_SOURCE != null && !DATA_SOURCE.isClosed()) {
            DATA_SOURCE.close();
        }
    }

    private static String env(String key, String defaultValue) {
        String v = System.getenv(key);
        return (v != null && !v.isBlank()) ? v : defaultValue;
    }
}
