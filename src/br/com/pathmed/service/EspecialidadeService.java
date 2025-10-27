package br.com.pathmed.service;

import br.com.pathmed.dao.EspecialidadeDAO;
import br.com.pathmed.model.Especialidade;
import java.util.List;
import java.util.Optional;

public class EspecialidadeService {
    private EspecialidadeDAO especialidadeDAO;

    public EspecialidadeService() {
        this.especialidadeDAO = new EspecialidadeDAO();
    }

    // Listar todas as especialidades
    public List<Especialidade> listarTodasEspecialidades() {
        return especialidadeDAO.findAll();
    }

    // Buscar especialidade por ID
    public Optional<Especialidade> buscarEspecialidadePorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID da especialidade não pode ser nulo");
        }
        return especialidadeDAO.findById(id);
    }
}