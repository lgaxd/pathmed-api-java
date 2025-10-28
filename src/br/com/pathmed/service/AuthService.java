package br.com.pathmed.service;

import br.com.pathmed.dao.AuthDAO;
import br.com.pathmed.model.LoginRequest;
import br.com.pathmed.model.LoginResponse;
import br.com.pathmed.model.RegistroPacienteRequest;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class AuthService {
    private AuthDAO authDAO;

    public AuthService() {
        this.authDAO = new AuthDAO();
    }

    /**
     * Realiza login baseado no tipo de usuário
     */
    public LoginResponse login(LoginRequest loginRequest) {
        // Validações básicas
        if (loginRequest.getUsuario() == null || loginRequest.getUsuario().trim().isEmpty()) {
            return new LoginResponse(false, "Usuário é obrigatório");
        }

        if (loginRequest.getSenha() == null || loginRequest.getSenha().trim().isEmpty()) {
            return new LoginResponse(false, "Senha é obrigatória");
        }

        if (loginRequest.getTipoUsuario() == null) {
            return new LoginResponse(false, "Tipo de usuário é obrigatório");
        }

        // Realiza login baseado no tipo
        switch (loginRequest.getTipoUsuario().toUpperCase()) {
            case "PACIENTE":
                return authDAO.loginPaciente(loginRequest);

            case "COLABORADOR":
                return authDAO.loginColaborador(loginRequest);

            default:
                return new LoginResponse(false, "Tipo de usuário inválido. Use 'PACIENTE' ou 'COLABORADOR'");
        }
    }

    /**
     * Registra novo paciente no sistema
     */
    public LoginResponse registrarPaciente(RegistroPacienteRequest request) {
        try {
            System.out.println("\n🔍 INICIANDO REGISTRO DE PACIENTE");

            // Primeiro faz o debug dos dados
            authDAO.debugRegistroPaciente(request);

            // Validações
            String validacao = validarDadosRegistro(request);
            if (validacao != null) {
                System.out.println("❌ Validação falhou: " + validacao);
                return new LoginResponse(false, validacao);
            }

            // Verifica se usuário já existe
            if (authDAO.verificarUsuarioExistente(request.getUsuario())) {
                System.out.println("❌ Usuário já existe: " + request.getUsuario());
                return new LoginResponse(false, "Usuário já está em uso");
            }

            // Verifica se CPF já existe
            if (authDAO.verificarCpfExistente(request.getCpfPaciente())) {
                System.out.println("❌ CPF já existe: " + request.getCpfPaciente());
                return new LoginResponse(false, "CPF já cadastrado");
            }

            // Verifica se RGHC já existe
            if (authDAO.verificarRghcExistente(request.getIdentificadorRghc())) {
                System.out.println("❌ RGHC já existe: " + request.getIdentificadorRghc());
                return new LoginResponse(false, "Identificador RGHC já cadastrado");
            }

            System.out.println("🔍 Todas as validações passaram, procedendo com registro...");

            // Registra o paciente
            boolean sucesso = authDAO.registrarPaciente(request);

            if (sucesso) {
                System.out.println("✅ Registro concluído com sucesso!");
                return new LoginResponse(true, "Paciente registrado com sucesso");
            } else {
                System.out.println("❌ Registro falhou sem exceção");
                return new LoginResponse(false, "Erro ao registrar paciente");
            }

        } catch (Exception e) {
            System.out.println("❌ Exceção no registro: " + e.getMessage());
            e.printStackTrace();
            return new LoginResponse(false, "Erro no registro: " + e.getMessage());
        }
    }

    /**
     * Valida dados do registro
     */
    private String validarDadosRegistro(RegistroPacienteRequest request) {
        // Validações de campos obrigatórios
        if (request.getIdentificadorRghc() == null || request.getIdentificadorRghc().trim().isEmpty()) {
            return "Identificador RGHC é obrigatório";
        }

        if (request.getIdentificadorRghc() != null && request.getIdentificadorRghc().length() > 10) {
            return "Identificador RGHC deve ter no máximo 10 caracteres";
        }

        if (request.getCpfPaciente() == null || request.getCpfPaciente().trim().isEmpty()) {
            return "CPF é obrigatório";
        }

        if (request.getCpfPaciente().length() != 11 || !request.getCpfPaciente().matches("\\d+")) {
            return "CPF deve conter 11 dígitos numéricos";
        }

        if (request.getNomePaciente() == null || request.getNomePaciente().trim().isEmpty()) {
            return "Nome é obrigatório";
        }

        if (request.getDataNascimento() == null) {
            return "Data de nascimento é obrigatória";
        }

        if (request.getDataNascimento().isAfter(LocalDate.now())) {
            return "Data de nascimento não pode ser futura";
        }

        if (request.getTipoSanguineo() == null || request.getTipoSanguineo().trim().isEmpty()) {
            return "Tipo sanguíneo é obrigatório";
        }

        if (!validarTipoSanguineo(request.getTipoSanguineo())) {
            return "Tipo sanguíneo inválido. Use: A+, A-, B+, B-, AB+, AB-, O+, O-";
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return "Email é obrigatório";
        }

        if (!validarEmail(request.getEmail())) {
            return "Email inválido";
        }

        if (request.getTelefone() == null || request.getTelefone().trim().isEmpty()) {
            return "Telefone é obrigatório";
        }

        if (request.getUsuario() == null || request.getUsuario().trim().isEmpty()) {
            return "Usuário é obrigatório";
        }

        if (request.getUsuario().length() < 3) {
            return "Usuário deve ter pelo menos 3 caracteres";
        }

        if (request.getSenha() == null || request.getSenha().trim().isEmpty()) {
            return "Senha é obrigatória";
        }

        if (request.getSenha().length() < 6) {
            return "Senha deve ter pelo menos 6 caracteres";
        }

        return null; // Todas as validações passaram
    }

    /**
     * Valida formato de email
     */
    private boolean validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(regex).matcher(email).matches();
    }

    /**
     * Valida tipo sanguíneo
     */
    private boolean validarTipoSanguineo(String tipoSanguineo) {
        String[] tiposValidos = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String tipo : tiposValidos) {
            if (tipo.equals(tipoSanguineo.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}