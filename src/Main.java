import br.com.pathmed.service.DisponibilidadeService;
import br.com.pathmed.model.DisponibilidadeDia;
import br.com.pathmed.model.HorarioDisponivel;
import br.com.pathmed.model.ProfissionalResumido;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static DisponibilidadeService disponibilidadeService = new DisponibilidadeService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("🚀 PATHMED - SISTEMA DE AGENDAMENTO");
        System.out.println("=====================================\n");

        exibirMenuPrincipal();
    }

    private static void exibirMenuPrincipal() {
        while (true) {
            System.out.println("\n📋 MENU PRINCIPAL");
            System.out.println("1️⃣  - Testar disponibilidade para HOJE");
            System.out.println("2️⃣  - Testar disponibilidade para AMANHÃ");
            System.out.println("3️⃣  - Testar disponibilidade para data específica");
            System.out.println("4️⃣  - Testar múltiplas especialidades");
            System.out.println("5️⃣  - Teste automático (demonstração)");
            System.out.println("0️⃣  - Sair");
            System.out.print("\nEscolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    testarDisponibilidadeHoje();
                    break;
                case "2":
                    testarDisponibilidadeAmanha();
                    break;
                case "3":
                    testarDisponibilidadeDataEspecifica();
                    break;
                case "4":
                    testarMultiplasEspecialidades();
                    break;
                case "5":
                    executarTesteAutomatico();
                    break;
                case "0":
                    System.out.println("👋 Saindo do sistema...");
                    return;
                default:
                    System.out.println("❌ Opção inválida!");
            }
        }
    }

    /**
     * Teste 1: Disponibilidade para HOJE
     */
    private static void testarDisponibilidadeHoje() {
        System.out.println("\n📅 TESTE - DISPONIBILIDADE PARA HOJE");
        System.out.println("=====================================");

        // Especialidades para testar
        Long[] especialidades = {1L, 2L, 3L}; // Cardiologia, Dermatologia, Pediatria

        for (Long espId : especialidades) {
            try {
                System.out.println("\n🔍 Buscando disponibilidade para especialidade ID: " + espId);
                DisponibilidadeDia disponibilidade = disponibilidadeService.buscarDisponibilidadeHoje(espId);
                exibirResultadoDisponibilidade(disponibilidade);

                // Pequena pausa para melhor visualização
                Thread.sleep(500);

            } catch (Exception e) {
                System.out.println("❌ Erro na especialidade " + espId + ": " + e.getMessage());
            }
        }
    }

    /**
     * Teste 2: Disponibilidade para AMANHÃ
     */
    private static void testarDisponibilidadeAmanha() {
        System.out.println("\n📅 TESTE - DISPONIBILIDADE PARA AMANHÃ");
        System.out.println("=======================================");

        LocalDate amanha = LocalDate.now().plusDays(1);
        System.out.println("Data de amanhã: " + amanha);

        // Testar Cardiologia (ID 1)
        try {
            DisponibilidadeDia disponibilidade = disponibilidadeService.buscarDisponibilidadeAmanha(1L);
            exibirResultadoDisponibilidade(disponibilidade);
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
    }

    /**
     * Teste 3: Disponibilidade para data específica
     */
    private static void testarDisponibilidadeDataEspecifica() {
        System.out.println("\n📅 TESTE - DATA ESPECÍFICA");
        System.out.println("============================");

        // Datas para testar
        LocalDate[] datasTeste = {
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(7)
        };

        for (LocalDate data : datasTeste) {
            System.out.println("\n🔍 Data: " + data);
            try {
                DisponibilidadeDia disponibilidade = disponibilidadeService.buscarDisponibilidadeDia(data, 1L);
                exibirResultadoDisponibilidade(disponibilidade);

                Thread.sleep(300);

            } catch (Exception e) {
                System.out.println("❌ Erro: " + e.getMessage());
            }
        }
    }

    /**
     * Teste 4: Múltiplas especialidades na mesma data
     */
    private static void testarMultiplasEspecialidades() {
        System.out.println("\n🏥 TESTE - MÚLTIPLAS ESPECIALIDADES");
        System.out.println("===================================");

        LocalDate dataTeste = LocalDate.now().plusDays(3);
        Long[] especialidades = {1L, 2L, 3L, 4L}; // Várias especialidades

        System.out.println("Data do teste: " + dataTeste);
        System.out.println("Especialidades: Cardiologia, Dermatologia, Pediatria, Ortopedia\n");

        for (Long espId : especialidades) {
            try {
                System.out.println("🎯 ESPECIALIDADE ID: " + espId);
                DisponibilidadeDia disponibilidade = disponibilidadeService.buscarDisponibilidadeDia(dataTeste, espId);

                // Mostra apenas resumo para comparação rápida
                String relatorio = disponibilidadeService.gerarRelatorioDisponibilidade(disponibilidade);
                System.out.println(relatorio);
                System.out.println("---");

                Thread.sleep(400);

            } catch (Exception e) {
                System.out.println("❌ Erro na especialidade " + espId + ": " + e.getMessage());
            }
        }
    }

    /**
     * Teste 5: Demonstração automática completa
     */
    private static void executarTesteAutomatico() {
        System.out.println("\n🎭 TESTE AUTOMÁTICO - DEMONSTRAÇÃO COMPLETA");
        System.out.println("============================================");

        // Cenário 1: Cardiologia hoje
        System.out.println("\n1. 📋 CENÁRIO: CARDIOLOGIA HOJE");
        System.out.println("--------------------------------");
        testarCenarioEspecifico(LocalDate.now(), 1L, "Cardiologia");

        // Cenário 2: Dermatologia amanhã
        System.out.println("\n2. 📋 CENÁRIO: DERMATOLOGIA AMANHÃ");
        System.out.println("-----------------------------------");
        testarCenarioEspecifico(LocalDate.now().plusDays(1), 2L, "Dermatologia");

        // Cenário 3: Pediatria em 2 dias
        System.out.println("\n3. 📋 CENÁRIO: PEDIATRIA EM 2 DIAS");
        System.out.println("-----------------------------------");
        testarCenarioEspecifico(LocalDate.now().plusDays(2), 3L, "Pediatria");

        System.out.println("\n✅ TESTE AUTOMÁTICO CONCLUÍDO!");
    }

    /**
     * Método auxiliar para testar um cenário específico
     */
    private static void testarCenarioEspecifico(LocalDate data, Long especialidadeId, String nomeEspecialidade) {
        try {
            System.out.println("📅 Data: " + data + " | 🏥 " + nomeEspecialidade);

            DisponibilidadeDia disponibilidade = disponibilidadeService.buscarDisponibilidadeDia(data, especialidadeId);

            // Relatório resumido
            String relatorio = disponibilidadeService.gerarRelatorioDisponibilidade(disponibilidade);
            System.out.println(relatorio);

            // Mostra alguns horários de exemplo
            System.out.println("\n🕐 EXEMPLOS DE HORÁRIOS:");
            int horariosMostrados = 0;
            for (HorarioDisponivel horario : disponibilidade.getHorarios()) {
                if (horario.hasDisponibilidade() && horariosMostrados < 3) {
                    String hora = horario.getDataHora().toLocalTime().toString();
                    int qtdProfissionais = horario.getProfissionaisDisponiveis().size();
                    System.out.printf("   ⏰ %s - %d profissional(es)\n", hora, qtdProfissionais);
                    horariosMostrados++;
                }
            }

            if (horariosMostrados == 0) {
                System.out.println("   😔 Nenhum horário disponível neste dia");
            }

        } catch (Exception e) {
            System.out.println("❌ Erro no cenário: " + e.getMessage());
        }
    }

    /**
     * Exibe o resultado completo da disponibilidade
     */
    private static void exibirResultadoDisponibilidade(DisponibilidadeDia disponibilidade) {
        if (disponibilidade == null) {
            System.out.println("❌ Nenhum dado de disponibilidade retornado");
            return;
        }

        // Cabeçalho
        System.out.println("\n" + "═".repeat(50));
        System.out.println("🏥 " + disponibilidade.getNomeEspecialidade().toUpperCase());
        System.out.println("📅 " + disponibilidade.getData());
        System.out.println("═".repeat(50));

        // Estatísticas
        String relatorio = disponibilidadeService.gerarRelatorioDisponibilidade(disponibilidade);
        System.out.println(relatorio);
        System.out.println();

        // Horários disponíveis (apenas os que têm profissionais)
        System.out.println("🕐 HORÁRIOS COM DISPONIBILIDADE:");
        System.out.println("-".repeat(40));

        boolean encontrouDisponibilidade = false;

        for (HorarioDisponivel horario : disponibilidade.getHorarios()) {
            if (horario.hasDisponibilidade()) {
                encontrouDisponibilidade = true;
                String hora = horario.getDataHora().toLocalTime().toString();
                int qtdProfissionais = horario.getProfissionaisDisponiveis().size();

                System.out.printf("⏰ %s - %d profissional(es) disponível(is)\n", hora, qtdProfissionais);

                // Lista os profissionais (máximo 3 para não poluir)
                int profissionaisListados = 0;
                for (ProfissionalResumido prof : horario.getProfissionaisDisponiveis()) {
                    if (profissionaisListados < 2) { // Mostra apenas 2 por horário
                        System.out.printf("   👨‍⚕️ %s\n", prof.getNomeProfissional());
                        profissionaisListados++;
                    }
                }

                if (profissionaisListados < qtdProfissionais) {
                    System.out.printf("   ... e mais %d profissional(es)\n", qtdProfissionais - profissionaisListados);
                }
                System.out.println();
            }
        }

        if (!encontrouDisponibilidade) {
            System.out.println("😔 Nenhum horário disponível neste dia");
            System.out.println();
        }

        System.out.println("🎯 PRÓXIMOS PASSOS:");
        System.out.println("   • Front-end mostra esses horários em um calendário");
        System.out.println("   • Paciente seleciona horário desejado");
        System.out.println("   • Sistema inicia processo de agendamento");
        System.out.println("═".repeat(50));
    }
}