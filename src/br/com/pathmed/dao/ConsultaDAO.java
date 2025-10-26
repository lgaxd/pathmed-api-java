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
        String sql = "INSERT INTO TB_PATHMED_TELECONSULTA (ID_CONSULTA, ID_PACIENTE, ID_PROFISSIONAL, ID_STATUS, DATA_HORA_CONSULTA) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, consulta.getIdConsulta());
            stmt.setLong(2, consulta.getIdPaciente());
            stmt.setLong(3, consulta.getIdProfissional());
            stmt.setLong(4, consulta.getIdStatus());
            stmt.setTimestamp(5, Timestamp.valueOf(consulta.getDataHoraConsulta()));

            stmt.executeUpdate();
        } catch (SQLException e) {
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
                "AND ABS(EXTRACT(MINUTE FROM (DATA_HORA_CONSULTA - ?))) < 30 " + // dentro de 30 minutos
                "AND ID_STATUS IN (1, 2, 3)"; // Agendada, Confirmada, Em Andamento

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            Timestamp timestamp = Timestamp.valueOf(dataHora);
            stmt.setLong(1, profissionalId);
            stmt.setTimestamp(2, timestamp);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar conflito de horário", e);
        }
        return false;
    }
}