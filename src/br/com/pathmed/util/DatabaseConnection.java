package br.com.pathmed.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());

    private static Connection conn = null;

    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() {

        try {
            if (conn == null || !conn.isValid(1)) {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                logger.log(Level.FINE,"Conectado ao Banco de Dados (Oracle)!");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Falha na conexão com o Banco de Dados\nVerifique seus dados...", e);
        }
        return conn;
    }
}