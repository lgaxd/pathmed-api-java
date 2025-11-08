package br.com.pathmed.dao;

import br.com.pathmed.model.Profissional;
import br.com.pathmed.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfissionalDAO {

    // GET /profissionais - Listar todos os profissionais
    public List<Profissional> findAll() {
        Connection connection = DatabaseConnection.getConnection();
        List<Profissional> profissionais = new ArrayList<>();
        String sql = "SELECT * FROM TB_PATHMED_PROFISSIONAL_SAUDE ORDER BY NOME_PROFISSIONAL_SAUDE";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Profissional profissional = mapResultSetToProfissional(rs);
                profissionais.add(profissional);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar profissionais", e);
        }
        return profissionais;
    }

    // Buscar profissional por ID
    public Optional<Profissional> findById(Long id) {
        Connection connection = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM TB_PATHMED_PROFISSIONAL_SAUDE WHERE ID_PROFISSIONAL = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Profissional profissional = mapResultSetToProfissional(rs);
                    return Optional.of(profissional);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar profissional por ID", e);
        }
        return Optional.empty();
    }

    // Buscar profissionais por especialidade
    public List<Profissional> findByEspecialidade(Long especialidadeId) {
        Connection connection = DatabaseConnection.getConnection();
        List<Profissional> profissionais = new ArrayList<>();
        String sql = "SELECT * FROM TB_PATHMED_PROFISSIONAL_SAUDE WHERE ID_ESPECIALIDADE = ? ORDER BY NOME_PROFISSIONAL_SAUDE";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, especialidadeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Profissional profissional = mapResultSetToProfissional(rs);
                    profissionais.add(profissional);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar profissionais por especialidade", e);
        }
        return profissionais;
    }

    // Buscar profissional por CPF
    public Optional<Profissional> findByCpf(String cpf) {
        Connection connection = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM TB_PATHMED_PROFISSIONAL_SAUDE WHERE CPF_PROFISSIONAL = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Profissional profissional = mapResultSetToProfissional(rs);
                    return Optional.of(profissional);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar profissional por CPF", e);
        }
        return Optional.empty();
    }

    // Buscar profissionais por nome (busca parcial)
    public List<Profissional> findByNome(String nome) {
        Connection connection = DatabaseConnection.getConnection();
        List<Profissional> profissionais = new ArrayList<>();
        String sql = "SELECT * FROM TB_PATHMED_PROFISSIONAL_SAUDE WHERE UPPER(NOME_PROFISSIONAL_SAUDE) LIKE UPPER(?) ORDER BY NOME_PROFISSIONAL_SAUDE";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Profissional profissional = mapResultSetToProfissional(rs);
                    profissionais.add(profissional);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar profissionais por nome", e);
        }
        return profissionais;
    }

    // Método auxiliar para mapear ResultSet para Profissional
    private Profissional mapResultSetToProfissional(ResultSet rs) throws SQLException {
        return new Profissional(
                rs.getLong("ID_PROFISSIONAL"),
                rs.getLong("ID_ESPECIALIDADE"),
                rs.getString("CPF_PROFISSIONAL"),
                rs.getString("EMAIL_CORPORATIVO_PROFISSIONAL"),
                rs.getString("NOME_PROFISSIONAL_SAUDE")
        );
    }
}