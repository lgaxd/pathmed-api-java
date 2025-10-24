package br.com.pathmed.service;

import br.com.pathmed.dao.PacienteDAO;
import br.com.pathmed.model.Paciente;
import java.util.List;
import java.util.Optional;

public class PacienteService {
    private PacienteDAO pacienteDAO;

    public PacienteService() {
        this.pacienteDAO = new PacienteDAO();
    }

    public List<Paciente> listarTodosPacientes() {
        return pacienteDAO.findAll();
    }

    public Optional<Paciente> buscarPacientePorId(Long id) {
        return pacienteDAO.findById(id);
    }

//    public void cadastrarPaciente(Paciente paciente) {
//        // Validações de negócio
//        if (paciente.getCpfPaciente() == null || paciente.getCpfPaciente().length() != 11) {
//            throw new IllegalArgumentException("CPF inválido");
//        }
//
//        if (pacienteDAO.findByCpf(paciente.getCpfPaciente()).isPresent()) {
//            throw new IllegalArgumentException("CPF já cadastrado");
//        }
//
//        pacienteDAO.save(paciente);
//    }

    // service/PacienteService.java - Adicionar este método
    public List<Paciente> buscarPacientesPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }

        return pacienteDAO.findByNome(nome);
    }

}