import br.com.pathmed.service.AuthService;
import br.com.pathmed.model.RegistroPacienteRequest;
import br.com.pathmed.model.LoginResponse;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static AuthService authService = new AuthService();

    public static void main(String[] args) {
        System.out.println("🏥 PATHMED - TESTE DE REGISTRO");
        System.out.println("===============================\n");

        // Teste automático primeiro
        executarTesteAutomatico();

        // Depois menu interativo
        exibirMenuRegistro();
    }

    /**
     * Teste automático de registro
     */
    private static void executarTesteAutomatico() {
        System.out.println("🧪 TESTE AUTOMÁTICO DE REGISTRO");
        System.out.println("-------------------------------");

        RegistroPacienteRequest registro = new RegistroPacienteRequest();
        registro.setIdentificadorRghc(gerarRghcUnico());
        registro.setCpfPaciente(gerarCpfUnico());
        registro.setNomePaciente("João Silva Teste");
        registro.setDataNascimento(LocalDate.of(1990, 5, 15));
        registro.setTipoSanguineo("A+");
        registro.setEmail("joao.silva." + System.currentTimeMillis() + "@email.com");
        registro.setTelefone("11992796717");
        registro.setUsuario("joao.silva." + System.currentTimeMillis());
        registro.setSenha("senha123");

        System.out.println("📝 Dados do teste:");
        System.out.println("   Nome: " + registro.getNomePaciente());
        System.out.println("   CPF: " + registro.getCpfPaciente());
        System.out.println("   RGHC: " + registro.getIdentificadorRghc());
        System.out.println("   Email: " + registro.getEmail());
        System.out.println("   Usuário: " + registro.getUsuario());

        System.out.println("\n🔄 Executando registro...");
        LoginResponse resposta = authService.registrarPaciente(registro);

        System.out.println("\n📋 RESULTADO:");
        System.out.println("   Sucesso: " + (resposta.isSucesso() ? "✅ SIM" : "❌ NÃO"));
        System.out.println("   Mensagem: " + resposta.getMensagem());
        System.out.println("\n" + "=" .repeat(50) + "\n");
    }

    /**
     * Menu interativo para testes de registro
     */
    private static void exibirMenuRegistro() {
        while (true) {
            System.out.println("🎯 MENU DE REGISTRO");
            System.out.println("1. 👤 Registrar Novo Paciente");
            System.out.println("2. 🔍 Debug Sequences");
            System.out.println("3. 🚪 Sair");
            System.out.print("\nEscolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    registrarPacienteInterativo();
                    break;
                case "2":
                    debugSequences();
                    break;
                case "3":
                    System.out.println("👋 Saindo...");
                    return;
                default:
                    System.out.println("❌ Opção inválida!\n");
            }
        }
    }

    private static void registrarPacienteInterativo() {
        System.out.println("\n👤 REGISTRO DE PACIENTE");
        System.out.println("----------------------");

        RegistroPacienteRequest registro = new RegistroPacienteRequest();

        System.out.print("Nome completo: ");
        registro.setNomePaciente(scanner.nextLine());

        System.out.print("CPF (11 dígitos): ");
        registro.setCpfPaciente(scanner.nextLine());

        System.out.print("Data nascimento (YYYY-MM-DD): ");
        registro.setDataNascimento(LocalDate.parse(scanner.nextLine()));

        System.out.print("Tipo sanguíneo (A+, A-, B+, B-, AB+, AB-, O+, O-): ");
        registro.setTipoSanguineo(scanner.nextLine());

        System.out.print("Email: ");
        registro.setEmail(scanner.nextLine());

        System.out.print("Telefone: ");
        registro.setTelefone(scanner.nextLine());

        System.out.print("Usuário: ");
        registro.setUsuario(scanner.nextLine());

        System.out.print("Senha: ");
        registro.setSenha(scanner.nextLine());

        // Gera RGHC automaticamente
        registro.setIdentificadorRghc(gerarRghcUnico());
        System.out.println("🔑 RGHC gerado: " + registro.getIdentificadorRghc());

        System.out.println("\n🔄 Processando registro...");
        LoginResponse resposta = authService.registrarPaciente(registro);

        System.out.println("\n📋 RESULTADO:");
        if (resposta.isSucesso()) {
            System.out.println("✅ " + resposta.getMensagem());
            System.out.println("🎉 Paciente registrado com sucesso!");
        } else {
            System.out.println("❌ " + resposta.getMensagem());
            System.out.println("💡 Verifique os dados e tente novamente.");
        }
        System.out.println();
    }

    private static void debugSequences() {
        System.out.println("\n🔍 DEBUG DAS SEQUENCES");
        System.out.println("---------------------");
        System.out.println();
    }

    // Método para gerar RGHC único (10 caracteres)
    private static String gerarRghcUnico() {
        return "RGHC" + (System.currentTimeMillis() % 1000000);
    }

    // Método para gerar CPF único
    private static String gerarCpfUnico() {
        // Gera um CPF aleatório para teste (não válido oficialmente)
        java.util.Random random = new java.util.Random();
        StringBuilder cpf = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            cpf.append(random.nextInt(10));
        }
        return cpf.toString();
    }
}