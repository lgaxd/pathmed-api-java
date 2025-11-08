package br.com.pathmed.controller;

import br.com.pathmed.model.Paciente;
import br.com.pathmed.model.PacienteResponse;
import br.com.pathmed.service.PacienteService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PacienteController {
    private PacienteService pacienteService;
    private ApiServer apiServer;
    private Gson gson;

    public PacienteController(ApiServer apiServer) {
        this.pacienteService = new PacienteService();
        this.apiServer = apiServer;
        this.gson = apiServer.getGson();
    }

    public void handlePacientes(HttpExchange exchange) throws IOException {

        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetPacientes(exchange);
                break;
            case "POST":
                handlePostPaciente(exchange);
                break;
            default:
                apiServer.sendJsonResponse(exchange, 405, "{\"error\": \"Método não permitido\"}");
        }
    }

    public void handlePacienteById(HttpExchange exchange) throws IOException {

        Long id = ApiServer.extractIdFromPath(exchange.getRequestURI().getPath());
        if (id == null) {
            apiServer.sendJsonResponse(exchange, 400, "{\"error\": \"ID inválido\"}");
            return;
        }

        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetPacienteById(exchange, id);
                break;
            case "PUT":
                handlePutPaciente(exchange, id);
                break;
            default:
                apiServer.sendJsonResponse(exchange, 405, "{\"error\": \"Método não permitido\"}");
        }
    }

    private void handleGetPacientes(HttpExchange exchange) throws IOException {
        try {
            List<Paciente> pacientes = pacienteService.listarPacientes();
            apiServer.sendJsonResponse(exchange, 200, gson.toJson(pacientes));
        } catch (Exception e) {
            String errorResponse = gson.toJson(new ErrorResponse("Erro ao listar pacientes: " + e.getMessage()));
            apiServer.sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    private void handlePostPaciente(HttpExchange exchange) throws IOException {
        try {
            String requestBody = ApiServer.readRequestBody(exchange);
            Paciente paciente = gson.fromJson(requestBody, Paciente.class);

            boolean success = pacienteService.cadastrarPaciente(paciente);

            if (success) {
                String response = gson.toJson(new SimpleResponse(true, "Paciente cadastrado com sucesso"));
                apiServer.sendJsonResponse(exchange, 201, response);
            } else {
                String response = gson.toJson(new SimpleResponse(false, "Erro ao cadastrar paciente"));
                apiServer.sendJsonResponse(exchange, 400, response);
            }
        } catch (Exception e) {
            String errorResponse = gson.toJson(new SimpleResponse(false, "Erro interno: " + e.getMessage()));
            apiServer.sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    private void handleGetPacienteById(HttpExchange exchange, Long id) throws IOException {
        try {
            Paciente paciente = pacienteService.obterPacientePorId(id);

            if (paciente != null) {
                apiServer.sendJsonResponse(exchange, 200, gson.toJson(paciente));
            } else {
                apiServer.sendJsonResponse(exchange, 404, "{\"error\": \"Paciente não encontrado\"}");
            }
        } catch (Exception e) {
            String errorResponse = gson.toJson(new ErrorResponse("Erro ao buscar paciente: " + e.getMessage()));
            apiServer.sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    private void handlePutPaciente(HttpExchange exchange, Long id) throws IOException {
        try {
            String requestBody = ApiServer.readRequestBody(exchange);
            Paciente paciente = gson.fromJson(requestBody, Paciente.class);
            paciente.setIdPaciente(id);

            boolean success = pacienteService.atualizarPaciente(paciente);

            if (success) {
                String response = gson.toJson(new SimpleResponse(true, "Paciente atualizado com sucesso"));
                apiServer.sendJsonResponse(exchange, 200, response);
            } else {
                String response = gson.toJson(new SimpleResponse(false, "Erro ao atualizar paciente"));
                apiServer.sendJsonResponse(exchange, 400, response);
            }
        } catch (Exception e) {
            String errorResponse = gson.toJson(new SimpleResponse(false, "Erro interno: " + e.getMessage()));
            apiServer.sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    // Classes para respostas
    private static class SimpleResponse {
        public boolean success;
        public String message;

        public SimpleResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    private static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}