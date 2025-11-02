import br.com.pathmed.controller.ApiServer;
import br.com.pathmed.util.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        try {
            // Testar conexão com o banco
            DatabaseConnection.getConnection().close();
            System.out.println("✅ Conexão com o banco estabelecida");

            // Iniciar servidor HTTP
            ApiServer server = new ApiServer();
            server.start(8080);

            System.out.println("🚀 API PathMed iniciada na porta 8080");
            System.out.println("📚 Endpoints disponíveis:");
            System.out.println("   POST   /auth/login");
            System.out.println("   POST   /auth/pacientes/register");
            System.out.println("   GET    /pacientes");
            System.out.println("   POST   /pacientes");
            System.out.println("   GET    /pacientes/{id}");
            System.out.println("   PUT    /pacientes/{id}");
            System.out.println("   GET    /consultas");
            System.out.println("   POST   /consultas");
            System.out.println("   PUT    /consultas/{id}/status");
            System.out.println("   GET    /consultas/paciente/{id}");
            System.out.println("   GET    /profissionais");
            System.out.println("   GET    /especialidades");
            System.out.println("   GET    /agenda/disponibilidade/{id}");

        } catch (Exception e) {
            System.err.println("❌ Erro ao iniciar a aplicação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}