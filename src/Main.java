import java.sql.Connection;
import java.sql.SQLException;
import br.com.pathmed.util.DatabaseConnection;

public class Main {

    public static void main(String[] args) {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Encerrando...");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
}
