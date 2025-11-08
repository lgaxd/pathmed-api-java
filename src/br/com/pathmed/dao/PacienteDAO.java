package br.com.pathmed.dao;

import br.com.pathmed.model.Paciente;
import br.com.pathmed.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacienteDAO {

    public List<Paciente> findAll() {
        Connection connection = DatabaseConnection.getConnection();
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
        Connection connection = DatabaseConnection.getConnection();
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
        String sql = "INSERT INTO TB_PATHMED_PACIENTE (IDENTIFICADOR_RGHC, CPF_PACIENTE, NOME_PACIENTE, DATA_NASCIMENTO, TIPO_SANGUINEO) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            System.out.println("💾 Salvando paciente no banco...");
            System.out.println("   RGHC: " + paciente.getIdentificadorRghc());
            System.out.println("   CPF: " + paciente.getCpfPaciente());
            System.out.println("   Nome: " + paciente.getNomePaciente());
            System.out.println("   Data Nasc: " + paciente.getDataNascimento());
            System.out.println("   Tipo Sanguíneo: " + paciente.getTipoSanguineo());

            stmt.setString(1, paciente.getIdentificadorRghc());
            stmt.setString(2, paciente.getCpfPaciente());
            stmt.setString(3, paciente.getNomePaciente());
            stmt.setDate(4, Date.valueOf(paciente.getDataNascimento()));
            stmt.setString(5, paciente.getTipoSanguineo());

            int rowsAffected = stmt.executeUpdate();
            System.out.println("✅ Linhas afetadas no INSERT: " + rowsAffected);

        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar paciente: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar paciente", e);
        }
    }

    public List<Paciente> findByNome(String nome) {
        Connection connection = DatabaseConnection.getConnection();
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

    public boolean update(Paciente paciente) {
        Connection connection = DatabaseConnection.getConnection();
        String sql = "UPDATE TB_PATHMED_PACIENTE SET IDENTIFICADOR_RGHC = ?, CPF_PACIENTE = ?, " +
                "NOME_PACIENTE = ?, DATA_NASCIMENTO = ?, TIPO_SANGUINEO = ? " +
                "WHERE ID_PACIENTE = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, paciente.getIdentificadorRghc());
            stmt.setString(2, paciente.getCpfPaciente());
            stmt.setString(3, paciente.getNomePaciente());
            stmt.setDate(4, Date.valueOf(paciente.getDataNascimento()));
            stmt.setString(5, paciente.getTipoSanguineo());
            stmt.setLong(6, paciente.getIdPaciente());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar paciente", e);
        }
    }

    public Optional<Paciente> findByIdentificadorRghc(String identificadorRghc) {
        Connection connection = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM TB_PATHMED_PACIENTE WHERE IDENTIFICADOR_RGHC = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, identificadorRghc);

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
            throw new RuntimeException("Erro ao buscar paciente por RGHC", e);
        }
        return Optional.empty();
    }

    public Optional<Paciente> findByCpf(String cpf) {
        Connection connection = DatabaseConnection.getConnection();
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

}