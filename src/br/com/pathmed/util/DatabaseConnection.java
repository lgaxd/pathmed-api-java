package br.com.pathmed.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() {

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conectado ao Banco de Dados (Oracle)!");

        } catch (SQLException e) {
            System.err.println("Falha na conexão com o Banco de Dados\nVerifique seus dados...");
            e.printStackTrace();
        }
        return conn;
    }
}