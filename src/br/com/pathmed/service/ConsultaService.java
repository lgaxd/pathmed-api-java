package br.com.pathmed.service;

import br.com.pathmed.dao.ConsultaDAO;
import br.com.pathmed.model.Consulta;
import br.com.pathmed.model.Paciente;

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
    public boolean agendarConsulta(Consulta consulta) {
        try {
            System.out.println("🔍 [1/6] Iniciando agendamento de consulta...");
            System.out.println("   📋 Dados recebidos:");
            System.out.println("      Paciente ID: " + consulta.getIdPaciente());
            System.out.println("      Profissional ID: " + consulta.getIdProfissional());
            System.out.println("      Data/Hora: " + consulta.getDataHoraConsulta());
            System.out.println("      Status: " + consulta.getIdStatus());

            // 1. Validações básicas
            System.out.println("🔍 [2/6] Executando validações básicas...");
            validarDadosConsulta(consulta);
            System.out.println("✅ Validações básicas passaram");

            // 2. Verifica se o paciente existe
            System.out.println("🔍 [3/6] Verificando existência do paciente ID: " + consulta.getIdPaciente());
            Optional<Paciente> pacienteOpt = pacienteService.buscarPacientePorId(consulta.getIdPaciente());
            if (pacienteOpt.isEmpty()) {
                System.out.println("❌ PACIENTE NÃO ENCONTRADO: " + consulta.getIdPaciente());
                return false;
            }
            System.out.println("✅ Paciente encontrado: " + pacienteOpt.get().getNomePaciente());

            // 3. Verifica se a data/hora é futura
            System.out.println("🔍 [4/6] Verificando data/hora...");
            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime dataMinima = agora.plusMinutes(15);
            System.out.println("   Agora: " + agora);
            System.out.println("   Data mínima permitida: " + dataMinima);
            System.out.println("   Data da consulta: " + consulta.getDataHoraConsulta());

            if (consulta.getDataHoraConsulta().isBefore(dataMinima)) {
                System.out.println("❌ DATA/HORA MUITO PRÓXIMA: A consulta deve ser agendada com pelo menos 15 minutos de antecedência");
                return false;
            }
            System.out.println("✅ Data/hora válida");

            // 4. Verifica conflito de horário
            System.out.println("🔍 [5/6] Verificando conflito de horário...");
            boolean conflito = consultaDAO.existsConflitoHorario(consulta.getIdProfissional(), consulta.getDataHoraConsulta());
            if (conflito) {
                System.out.println("❌ CONFLITO DE HORÁRIO: Já existe uma consulta agendada para este profissional neste horário");
                return false;
            }
            System.out.println("✅ Nenhum conflito de horário encontrado");

            // 5. Define status padrão
            System.out.println("🔍 [6/6] Definindo status e salvando...");
            consulta.setIdStatus(1L); // Agendada
            System.out.println("✅ Status definido como: Agendada (ID 1)");

            // 6. Salva a consulta
            consultaDAO.save(consulta);
            System.out.println("🎉 CONSULTA AGENDADA COM SUCESSO!");

            return true;

        } catch (Exception e) {
            System.err.println("❌ ERRO CRÍTICO ao agendar consulta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
        if (consulta.getIdPaciente() == null || consulta.getIdPaciente() <= 0) {
            throw new IllegalArgumentException("ID do paciente é obrigatório");
        }

        if (consulta.getIdProfissional() == null || consulta.getIdProfissional() <= 0) {
            throw new IllegalArgumentException("ID do profissional é obrigatório");
        }

        if (consulta.getDataHoraConsulta() == null) {
            throw new IllegalArgumentException("Data/hora da consulta é obrigatória");
        }

        if (consulta.getDataHoraConsulta().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data/hora da consulta não pode ser no passado");
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