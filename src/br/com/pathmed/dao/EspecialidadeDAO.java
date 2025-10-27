package br.com.pathmed.dao;

import br.com.pathmed.model.Especialidade;
import br.com.pathmed.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EspecialidadeDAO {
    private Connection connection;

    public EspecialidadeDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Listar todas as especialidades
    public List<Especialidade> findAll() {
        List<Especialidade> especialidades = new ArrayList<>();
        String sql = "SELECT * FROM TB_PATHMED_ESPECIALIDADE ORDER BY DESCRICAO_ESPECIALIDADE";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Especialidade especialidade = new Especialidade(
                        rs.getLong("ID_ESPECIALIDADE"),
                        rs.getString("DESCRICAO_ESPECIALIDADE")
                );
                especialidades.add(especialidade);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar especialidades", e);
        }
        return especialidades;
    }

    // Buscar especialidade por ID
    public Optional<Especialidade> findById(Long id) {
        String sql = "SELECT * FROM TB_PATHMED_ESPECIALIDADE WHERE ID_ESPECIALIDADE = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Especialidade especialidade = new Especialidade(
                            rs.getLong("ID_ESPECIALIDADE"),
                            rs.getString("DESCRICAO_ESPECIALIDADE")
                    );
                    return Optional.of(especialidade);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar especialidade por ID", e);
        }
        return Optional.empty();
    }
}