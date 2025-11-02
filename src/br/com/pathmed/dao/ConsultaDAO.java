package br.com.pathmed.dao;

import br.com.pathmed.util.DatabaseConnection;
import br.com.pathmed.model.Consulta;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultaDAO {
    private Connection connection;

    public ConsultaDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // GET /consultas - Listar todas as consultas
    public List<Consulta> findAll() {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT * FROM TB_PATHMED_TELECONSULTA ORDER BY DATA_HORA_CONSULTA DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Consulta consulta = mapResultSetToConsulta(rs);
                consultas.add(consulta);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar consultas", e);
        }
        return consultas;
    }

    // GET /consultas/{id} - Buscar consulta por ID
    public Optional<Consulta> findById(Long id) {
        String sql = "SELECT * FROM TB_PATHMED_TELECONSULTA WHERE ID_CONSULTA = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Consulta consulta = mapResultSetToConsulta(rs);
                    return Optional.of(consulta);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar consulta por ID", e);
        }
        return Optional.empty();
    }

    // GET /consultas/paciente/{id} - Consultas por paciente
    public List<Consulta> findByPacienteId(Long pacienteId) {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT * FROM TB_PATHMED_TELECONSULTA WHERE ID_PACIENTE = ? ORDER BY DATA_HORA_CONSULTA DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pacienteId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Consulta consulta = mapResultSetToConsulta(rs);
                    consultas.add(consulta);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar consultas do paciente", e);
        }
        return consultas;
    }

    // POST /consultas - Agendar nova consulta
    public void save(Consulta consulta) {
        String sql = "INSERT INTO TB_PATHMED_TELECONSULTA (ID_PACIENTE, ID_PROFISSIONAL, ID_STATUS, DATA_HORA_CONSULTA) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("💾 Salvando consulta no banco...");
            System.out.println("   ID Paciente: " + consulta.getIdPaciente());
            System.out.println("   ID Profissional: " + consulta.getIdProfissional());
            System.out.println("   ID Status: " + consulta.getIdStatus());
            System.out.println("   Data/Hora: " + consulta.getDataHoraConsulta());

            stmt.setLong(1, consulta.getIdPaciente());
            stmt.setLong(2, consulta.getIdProfissional());
            stmt.setLong(3, consulta.getIdStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(consulta.getDataHoraConsulta()));

            int rowsAffected = stmt.executeUpdate();
            System.out.println("✅ Linhas afetadas no INSERT: " + rowsAffected);

        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar consulta: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao agendar consulta", e);
        }
    }

    // PUT /consultas/{id}/status - Atualizar status da consulta
    public boolean updateStatus(Long consultaId, Long novoStatusId) {
        String sql = "UPDATE TB_PATHMED_TELECONSULTA SET ID_STATUS = ? WHERE ID_CONSULTA = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, novoStatusId);
            stmt.setLong(2, consultaId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da consulta", e);
        }
    }

    // Método auxiliar para mapear ResultSet para Consulta
    private Consulta mapResultSetToConsulta(ResultSet rs) throws SQLException {
        return new Consulta(
                rs.getLong("ID_CONSULTA"),
                rs.getLong("ID_PACIENTE"),
                rs.getLong("ID_PROFISSIONAL"),
                rs.getLong("ID_STATUS"),
                rs.getTimestamp("DATA_HORA_CONSULTA").toLocalDateTime()
        );
    }

    // Método para verificar conflito de horário
    public boolean existsConflitoHorario(Long profissionalId, LocalDateTime dataHora) {
        String sql = "SELECT COUNT(*) FROM TB_PATHMED_TELECONSULTA " +
                "WHERE ID_PROFISSIONAL = ? " +
                "AND ID_STATUS IN (1, 2, 3) " + // Agendada, Confirmada, Em Andamento
                "AND ABS(EXTRACT(DAY FROM (DATA_HORA_CONSULTA - ?)) * 1440 + " + // dias para minutos
                "EXTRACT(HOUR FROM (DATA_HORA_CONSULTA - ?)) * 60 + " + // horas para minutos
                "EXTRACT(MINUTE FROM (DATA_HORA_CONSULTA - ?))) < 30"; // dentro de 30 minutos

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            Timestamp timestamp = Timestamp.valueOf(dataHora);
            stmt.setLong(1, profissionalId);
            stmt.setTimestamp(2, timestamp);
            stmt.setTimestamp(3, timestamp);
            stmt.setTimestamp(4, timestamp);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("🔍 Conflitos encontrados para profissional " + profissionalId + ": " + count);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao verificar conflito de horário: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, assume que não há conflito para não bloquear o agendamento
            return false;
        }
        return false;
    }
}