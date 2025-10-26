import br.com.pathmed.model.Consulta;
import br.com.pathmed.service.ConsultaService;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        ConsultaService consultaService = new ConsultaService();

        System.out.println("=== TESTANDO CONFLITO DE HORÁRIO ===");

        try {
            // Primeira consulta
            Consulta consulta1 = new Consulta(
                    consultaService.getProximoId(),
                    1L, // paciente
                    1L, // profissional
                    1L,
                    LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0) // 10:00
            );

            consultaService.agendarConsulta(consulta1);
            System.out.println("Consulta 1 agendada para 10:00");

            // Tentativa de consulta no mesmo horário (deve falhar)
            Consulta consulta2 = new Consulta(
                    consultaService.getProximoId(),
                    2L, // outro paciente
                    1L, // mesmo profissional
                    1L,
                    LocalDateTime.now().plusDays(1).withHour(10).withMinute(5).withSecond(0).withNano(0) // 10:05 (dentro do intervalo de 30 min)
            );

            consultaService.agendarConsulta(consulta2);
            System.out.println("ERRO: Consulta 2 foi agendada (não deveria)");

        } catch (IllegalArgumentException e) {
            System.out.println("SUCESSO: Conflito detectado - " + e.getMessage());
        }

        // Teste de horário válido (fora do intervalo)
        try {
            Consulta consulta3 = new Consulta(
                    consultaService.getProximoId(),
                    3L, // outro paciente
                    1L, // mesmo profissional
                    1L,
                    LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0) // 11:00 (fora do intervalo)
            );

            consultaService.agendarConsulta(consulta3);
            System.out.println("SUCESSO: Consulta 3 agendada para 11:00 (sem conflito)");

        } catch (IllegalArgumentException e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }
}