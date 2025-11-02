package br.com.pathmed.service;

import br.com.pathmed.dao.PacienteDAO;
import br.com.pathmed.model.Paciente;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PacienteService {
    private PacienteDAO pacienteDAO;

    public PacienteService() {
        this.pacienteDAO = new PacienteDAO();
    }

    // Mantém o método original para uso interno
    public Optional<Paciente> buscarPacientePorId(Long id) {
        return pacienteDAO.findById(id);
    }

    // Novo método para uso no controller (retorna Paciente ou null)
    public Paciente obterPacientePorId(Long id) {
        return pacienteDAO.findById(id).orElse(null);
    }

    public List<Paciente> listarPacientes() {
        return pacienteDAO.findAll();
    }

    public boolean cadastrarPaciente(Paciente paciente) {
        // Verifica CPF nulo ou com tamanho inválido
        if (paciente.getCpfPaciente() == null || paciente.getCpfPaciente().length() != 11) {
            return false;
        }

        // Verifica se já existe um paciente com o mesmo CPF
        if (pacienteDAO.findByCpf(paciente.getCpfPaciente()).isPresent()) {
            return false;
        }

        // Salva e retorna verdadeiro se tudo deu certo
        pacienteDAO.save(paciente);
        return true;
    }

    public List<Paciente> buscarPacientesPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }

        return pacienteDAO.findByNome(nome);
    }

    public boolean atualizarPaciente(Paciente pacienteAtualizado) {
        // 1. Verificar se o paciente existe
        Optional<Paciente> pacienteExistente = pacienteDAO.findById(pacienteAtualizado.getIdPaciente());
        if (pacienteExistente.isEmpty()) {
            throw new IllegalArgumentException("Paciente não encontrado com ID: " + pacienteAtualizado.getIdPaciente());
        }

        // 2. Validar dados obrigatórios
        validarDadosPaciente(pacienteAtualizado);

        // 3. Verificar se CPF já existe em outro paciente (se foi alterado)
        Paciente pacienteAtual = pacienteExistente.get();
        if (!pacienteAtual.getCpfPaciente().equals(pacienteAtualizado.getCpfPaciente())) {
            Optional<Paciente> pacienteComCpf = pacienteDAO.findByCpf(pacienteAtualizado.getCpfPaciente());
            if (pacienteComCpf.isPresent() && !pacienteComCpf.get().getIdPaciente().equals(pacienteAtualizado.getIdPaciente())) {
                throw new IllegalArgumentException("CPF já cadastrado para outro paciente");
            }
        }

        // 4. Verificar se RGHC já existe em outro paciente (se foi alterado)
        if (!pacienteAtual.getIdentificadorRghc().equals(pacienteAtualizado.getIdentificadorRghc())) {
            Optional<Paciente> pacienteComRghc = pacienteDAO.findByIdentificadorRghc(pacienteAtualizado.getIdentificadorRghc());
            if (pacienteComRghc.isPresent() && !pacienteComRghc.get().getIdPaciente().equals(pacienteAtualizado.getIdPaciente())) {
                throw new IllegalArgumentException("Identificador RGHC já cadastrado para outro paciente");
            }
        }

        // 5. Atualizar paciente
        return pacienteDAO.update(pacienteAtualizado);
    }

    // Método auxiliar para validação
    private void validarDadosPaciente(Paciente paciente) {
        if (paciente.getNomePaciente() == null || paciente.getNomePaciente().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do paciente é obrigatório");
        }

        if (paciente.getCpfPaciente() == null || paciente.getCpfPaciente().length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        }

        if (paciente.getIdentificadorRghc() == null || paciente.getIdentificadorRghc().trim().isEmpty()) {
            throw new IllegalArgumentException("Identificador RGHC é obrigatório");
        }

        if (paciente.getDataNascimento() == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória");
        }

        if (paciente.getTipoSanguineo() == null || !isTipoSanguineoValido(paciente.getTipoSanguineo())) {
            throw new IllegalArgumentException("Tipo sanguíneo inválido");
        }
    }

    // Método auxiliar para validar tipo sanguíneo
    private boolean isTipoSanguineoValido(String tipoSanguineo) {
        String[] tiposValidos = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        return Arrays.asList(tiposValidos).contains(tipoSanguineo);
    }
}