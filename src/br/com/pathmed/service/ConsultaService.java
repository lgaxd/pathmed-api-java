package br.com.pathmed.service;

import br.com.pathmed.dao.ConsultaDAO;
import br.com.pathmed.model.Consulta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ConsultaService {
    private ConsultaDAO consultaDAO;
    private PacienteService pacienteService;

    public ConsultaService() {
        this.consultaDAO = new ConsultaDAO();
        this.pacienteService = new PacienteService();
    }

    // GET /consultas - Listar todas as consultas
    public List<Consulta> listarTodasConsultas() {
        return consultaDAO.findAll();
    }

    // GET /consultas/{id} - Buscar consulta por ID
    public Optional<Consulta> buscarConsultaPorId(Long id) {
        return consultaDAO.findById(id);
    }

    // GET /consultas/paciente/{id} - Consultas por paciente
    public List<Consulta> buscarConsultasPorPaciente(Long pacienteId) {
        // Verificar se o paciente existe
        if (pacienteService.buscarPacientePorId(pacienteId).isEmpty()) {
            throw new IllegalArgumentException("Paciente não encontrado com ID: " + pacienteId);
        }

        return consultaDAO.findByPacienteId(pacienteId);
    }

    // POST /consultas - Agendar nova consulta
    public void agendarConsulta(Consulta consulta) {
        // Validações
        validarDadosConsulta(consulta);

        // Verificar se paciente existe
        if (pacienteService.buscarPacientePorId(consulta.getIdPaciente()).isEmpty()) {
            throw new IllegalArgumentException("Paciente não encontrado");
        }

        // Verificar se a data/hora é futura (com margem de 15 minutos)
        if (consulta.getDataHoraConsulta().isBefore(LocalDateTime.now().plusMinutes(15))) {
            throw new IllegalArgumentException("A consulta deve ser agendada com pelo menos 15 minutos de antecedência");
        }

        // Verificar conflito de horário (30 minutos de intervalo)
        if (consultaDAO.existsConflitoHorario(consulta.getIdProfissional(), consulta.getDataHoraConsulta())) {
            throw new IllegalArgumentException("Profissional já possui consulta agendada em horário próximo (intervalo de 30 minutos)");
        }

        // Status padrão: 1 - Agendada
        consulta.setIdStatus(1L);

        consultaDAO.save(consulta);
    }

    // PUT /consultas/{id}/status - Atualizar status da consulta
    public boolean atualizarStatusConsulta(Long consultaId, Long novoStatusId) {
        // Verificar se a consulta existe
        Optional<Consulta> consultaOpt = consultaDAO.findById(consultaId);
        if (consultaOpt.isEmpty()) {
            throw new IllegalArgumentException("Consulta não encontrada com ID: " + consultaId);
        }

        // Validar status
        if (!isStatusValido(novoStatusId)) {
            throw new IllegalArgumentException("Status inválido: " + novoStatusId);
        }

        Consulta consulta = consultaOpt.get();

        // Validações específicas por status
        validarMudancaStatus(consulta.getIdStatus(), novoStatusId, consulta.getDataHoraConsulta());

        return consultaDAO.updateStatus(consultaId, novoStatusId);
    }

    // Métodos auxiliares de validação
    private void validarDadosConsulta(Consulta consulta) {
        if (consulta.getIdPaciente() == null) {
            throw new IllegalArgumentException("ID do paciente é obrigatório");
        }

        if (consulta.getIdProfissional() == null) {
            throw new IllegalArgumentException("ID do profissional é obrigatório");
        }

        if (consulta.getDataHoraConsulta() == null) {
            throw new IllegalArgumentException("Data e hora da consulta são obrigatórias");
        }
    }

    private boolean isStatusValido(Long statusId) {
        // Status válidos: 1-8 (conforme tabela TB_PATHMED_STATUS_CONSULTA)
        return statusId >= 1 && statusId <= 8;
    }

    private void validarMudancaStatus(Long statusAtual, Long novoStatus, LocalDateTime dataHoraConsulta) {
        // Não permitir alterar status de consultas concluídas ou canceladas
        if (statusAtual == 4L || statusAtual == 5L) { // Concluída ou Cancelada
            throw new IllegalArgumentException("Não é possível alterar status de consulta concluída ou cancelada");
        }

        // Não permitir marcar como "Concluída" se a data/hora for futura
        if (novoStatus == 4L && dataHoraConsulta.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível marcar consulta como concluída antes da data agendada");
        }
    }

    // Método para gerar próximo ID (simulação de sequence)
    public Long getProximoId() {
        // Em um sistema real, isso viria de uma sequence do banco
        // Aqui é uma simulação simples
        List<Consulta> todas = consultaDAO.findAll();
        if (todas.isEmpty()) {
            return 1L;
        }
        return todas.stream().mapToLong(Consulta::getIdConsulta).max().orElse(0L) + 1;
    }
}