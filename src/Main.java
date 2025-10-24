import java.sql.Connection;
import java.sql.SQLException;
import br.com.pathmed.util.DatabaseConnection;
import br.com.pathmed.service.PacienteService;
import br.com.pathmed.model.Paciente;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        PacienteService pacienteService = new PacienteService();
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null) {
            try {
                List<Paciente> pacientes = pacienteService.listarTodosPacientes();
                pacientes.forEach(p -> System.out.println(p.getNomePaciente()));
                connection.close();
                System.out.println("Encerrando...");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
}
