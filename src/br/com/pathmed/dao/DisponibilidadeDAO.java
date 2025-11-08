package br.com.pathmed.dao;
import br.com.pathmed.model.HorarioDisponivel;
import br.com.pathmed.model.ProfissionalResumido;
import br.com.pathmed.model.DisponibilidadeDia;
import br.com.pathmed.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class DisponibilidadeDAO {
    /**
     * Busca disponibilidade para um dia e especialidade específicos
     * Retorna todos os horários de 30min das 8:00 às 18:00 com profissionais disponíveis
     */
    public DisponibilidadeDia findDisponibilidadePorDia(LocalDate data, Long idEspecialidade) {
        // Primeiro busca info da especialidade
        String nomeEspecialidade = findNomeEspecialidadeById(idEspecialidade);
        DisponibilidadeDia disponibilidadeDia = new DisponibilidadeDia(data, idEspecialidade, nomeEspecialidade);

        // Gera todos os horários do dia (8:00 às 18:00, intervalos de 30min)
        List<HorarioDisponivel> horarios = gerarHorariosDoDia(data);

        // Para cada horário, busca profissionais disponíveis
        for (HorarioDisponivel horario : horarios) {
            List<ProfissionalResumido> profissionais = findProfissionaisDisponiveisNoHorario(
                    horario.getDataHora(), idEspecialidade);
            horario.setProfissionaisDisponiveis(profissionais);
        }

        disponibilidadeDia.setHorarios(horarios);
        return disponibilidadeDia;
    }

    /**
     * Gera lista de horários das 8:00 às 18:00 com intervalos de 30min
     */
    private List<HorarioDisponivel> gerarHorariosDoDia(LocalDate data) {
        List<HorarioDisponivel> horarios = new ArrayList<>();
        LocalTime horaInicio = LocalTime.of(8, 0); // 8:00
        LocalTime horaFim = LocalTime.of(18, 0);   // 18:00

        LocalTime horaAtual = horaInicio;
        while (horaAtual.isBefore(horaFim)) {
            LocalDateTime dataHora = LocalDateTime.of(data, horaAtual);
            horarios.add(new HorarioDisponivel(dataHora));
            horaAtual = horaAtual.plusMinutes(30); // Próximo horário em 30min
        }

        return horarios;
    }

    /**
     * Busca profissionais disponíveis em um horário específico
     */
    private List<ProfissionalResumido> findProfissionaisDisponiveisNoHorario(LocalDateTime dataHora, Long idEspecialidade) {
        Connection connection = DatabaseConnection.getConnection();
        List<ProfissionalResumido> profissionais = new ArrayList<>();

        String sql = "SELECT ps.ID_PROFISSIONAL, ps.NOME_PROFISSIONAL_SAUDE, e.DESCRICAO_ESPECIALIDADE " +
                "FROM TB_PATHMED_PROFISSIONAL_SAUDE ps " +
                "JOIN TB_PATHMED_ESPECIALIDADE e ON ps.ID_ESPECIALIDADE = e.ID_ESPECIALIDADE " +
                "WHERE ps.ID_ESPECIALIDADE = ? " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM TB_PATHMED_TELECONSULTA tc " +
                "    WHERE tc.ID_PROFISSIONAL = ps.ID_PROFISSIONAL " +
                "    AND tc.DATA_HORA_CONSULTA = ? " +
                "    AND tc.ID_STATUS IN (1, 2) " + // Agendada ou Confirmada
                ") " +
                "ORDER BY ps.NOME_PROFISSIONAL_SAUDE";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            Timestamp timestamp = Timestamp.valueOf(dataHora);
            stmt.setLong(1, idEspecialidade);
            stmt.setTimestamp(2, timestamp);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProfissionalResumido profissional = new ProfissionalResumido(
                            rs.getLong("ID_PROFISSIONAL"),
                            rs.getString("NOME_PROFISSIONAL_SAUDE"),
                            rs.getString("DESCRICAO_ESPECIALIDADE")
                    );
                    profissionais.add(profissional);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar profissionais disponíveis no horário", e);
        }

        return profissionais;
    }

    /**
     * Busca nome da especialidade por ID
     */
    private String findNomeEspecialidadeById(Long idEspecialidade) {
        Connection connection = DatabaseConnection.getConnection();
        String sql = "SELECT DESCRICAO_ESPECIALIDADE FROM TB_PATHMED_ESPECIALIDADE WHERE ID_ESPECIALIDADE = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idEspecialidade);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("DESCRICAO_ESPECIALIDADE");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar nome da especialidade", e);
        }

        return "Especialidade " + idEspecialidade;
    }

    /**
     * Busca dias com disponibilidade nos próximos 30 dias para uma especialidade
     * (Útil para o calendário do front-end mostrar quais dias têm horários disponíveis)
     */
    public List<LocalDate> findDiasComDisponibilidade(Long idEspecialidade, int diasNoFuturo) {
        Connection connection = DatabaseConnection.getConnection();
        List<LocalDate> diasDisponiveis = new ArrayList<>();
        LocalDate dataInicio = LocalDate.now();
        LocalDate dataFim = dataInicio.plusDays(diasNoFuturo);

        String sql = "SELECT DISTINCT TRUNC(tc.DATA_HORA_CONSULTA) as DIA " +
                "FROM TB_PATHMED_TELECONSULTA tc " +
                "JOIN TB_PATHMED_PROFISSIONAL_SAUDE ps ON tc.ID_PROFISSIONAL = ps.ID_PROFISSIONAL " +
                "WHERE ps.ID_ESPECIALIDADE = ? " +
                "AND tc.DATA_HORA_CONSULTA BETWEEN ? AND ? " +
                "AND tc.ID_STATUS IN (1, 2) " +
                "ORDER BY DIA";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idEspecialidade);
            stmt.setDate(2, java.sql.Date.valueOf(dataInicio));
            stmt.setDate(3, java.sql.Date.valueOf(dataFim));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate dia = rs.getDate("DIA").toLocalDate();
                    diasDisponiveis.add(dia);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar dias com disponibilidade", e);
        }

        return diasDisponiveis;
    }
}