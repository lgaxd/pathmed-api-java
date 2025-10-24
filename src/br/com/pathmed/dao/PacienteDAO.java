package br.com.pathmed.dao;

import br.com.pathmed.model.Paciente;
import br.com.pathmed.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacienteDAO {
    private Connection connection;

    public PacienteDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Paciente> findAll() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM TB_PATHMED_PACIENTE";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Paciente paciente = new Paciente(
                        rs.getLong("ID_PACIENTE"),
                        rs.getString("IDENTIFICADOR_RGHC"),
                        rs.getString("CPF_PACIENTE"),
                        rs.getString("NOME_PACIENTE"),
                        rs.getDate("DATA_NASCIMENTO").toLocalDate(),
                        rs.getString("TIPO_SANGUINEO")
                );
                pacientes.add(paciente);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pacientes", e);
        }
        return pacientes;
    }

    public Optional<Paciente> findById(Long id) {
        String sql = "SELECT * FROM TB_PATHMED_PACIENTE WHERE ID_PACIENTE = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Paciente paciente = new Paciente(
                        rs.getLong("ID_PACIENTE"),
                        rs.getString("IDENTIFICADOR_RGHC"),
                        rs.getString("CPF_PACIENTE"),
                        rs.getString("NOME_PACIENTE"),
                        rs.getDate("DATA_NASCIMENTO").toLocalDate(),
                        rs.getString("TIPO_SANGUINEO")
                );
                return Optional.of(paciente);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar paciente por ID", e);
        }
        return Optional.empty();
    }

    public void save(Paciente paciente) {
        String sql = "INSERT INTO TB_PATHMED_PACIENTE (ID_PACIENTE, IDENTIFICADOR_RGHC, CPF_PACIENTE, NOME_PACIENTE, DATA_NASCIMENTO, TIPO_SANGUINEO) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, paciente.getIdPaciente());
            stmt.setString(2, paciente.getIdentificadorRghc());
            stmt.setString(3, paciente.getCpfPaciente());
            stmt.setString(4, paciente.getNomePaciente());
            stmt.setDate(5, Date.valueOf(paciente.getDataNascimento()));
            stmt.setString(6, paciente.getTipoSanguineo());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar paciente", e);
        }
    }

    public List<Paciente> findByNome(String nome) {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM TB_PATHMED_PACIENTE WHERE UPPER(NOME_PACIENTE) LIKE UPPER(?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Paciente paciente = new Paciente(
                            rs.getLong("ID_PACIENTE"),
                            rs.getString("IDENTIFICADOR_RGHC"),
                            rs.getString("CPF_PACIENTE"),
                            rs.getString("NOME_PACIENTE"),
                            rs.getDate("DATA_NASCIMENTO").toLocalDate(),
                            rs.getString("TIPO_SANGUINEO")
                    );
                    pacientes.add(paciente);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pacientes por nome", e);
        }
        return pacientes;
    }

    // Busca por CPF
    public Optional<Paciente> findByCpf(String cpf) {
        String sql = "SELECT * FROM TB_PATHMED_PACIENTE WHERE CPF_PACIENTE = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Paciente paciente = new Paciente(
                            rs.getLong("ID_PACIENTE"),
                            rs.getString("IDENTIFICADOR_RGHC"),
                            rs.getString("CPF_PACIENTE"),
                            rs.getString("NOME_PACIENTE"),
                            rs.getDate("DATA_NASCIMENTO").toLocalDate(),
                            rs.getString("TIPO_SANGUINEO")
                    );
                    return Optional.of(paciente);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar paciente por CPF", e);
        }
        return Optional.empty();
    }

    // Busca por tipo sanguíneo
    public List<Paciente> findByTipoSanguineo(String tipoSanguineo) {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM TB_PATHMED_PACIENTE WHERE TIPO_SANGUINEO = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tipoSanguineo);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Paciente paciente = new Paciente(
                            rs.getLong("ID_PACIENTE"),
                            rs.getString("IDENTIFICADOR_RGHC"),
                            rs.getString("CPF_PACIENTE"),
                            rs.getString("NOME_PACIENTE"),
                            rs.getDate("DATA_NASCIMENTO").toLocalDate(),
                            rs.getString("TIPO_SANGUINEO")
                    );
                    pacientes.add(paciente);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pacientes por tipo sanguíneo", e);
        }
        return pacientes;
    }

    // Outros métodos: update, delete, findByCpf, etc.
}