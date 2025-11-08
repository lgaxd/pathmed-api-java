package br.com.pathmed.controller;

import br.com.pathmed.model.Consulta;
import br.com.pathmed.service.ConsultaService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsultaController {
    private ConsultaService consultaService;
    private ApiServer apiServer;
    private Gson gson;
    private static final Logger logger = Logger.getLogger(AgendaController.class.getName());

    public ConsultaController(ApiServer apiServer) {
        this.consultaService = new ConsultaService();
        this.apiServer = apiServer;
        this.gson = apiServer.getGson();
    }

    public void handleConsultas(HttpExchange exchange) throws IOException {

        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetConsultas(exchange);
                break;
            case "POST":
                handlePostConsulta(exchange);
                break;
            default:
                apiServer.sendJsonResponse(exchange, 405, "{\"error\": \"Método não permitido\"}");
        }
    }

    public void handleConsultaActions(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();

        if (path.contains("/status")) {
            // PUT /consultas/{id}/status
            Long id = extractConsultaIdFromPath(path);
            if (id == null) {
                apiServer.sendJsonResponse(exchange, 400, "{\"error\": \"ID inválido\"}");
                return;
            }

            if ("PUT".equals(exchange.getRequestMethod())) {
                handlePutConsultaStatus(exchange, id);
            } else {
                apiServer.sendJsonResponse(exchange, 405, "{\"error\": \"Método não permitido\"}");
            }

        } else if (path.contains("/paciente/")) {
            // GET /consultas/paciente/{id}
            Long pacienteId = extractPacienteIdFromPath(path);
            if (pacienteId == null) {
                apiServer.sendJsonResponse(exchange, 400, "{\"error\": \"ID do paciente inválido\"}");
                return;
            }

            if ("GET".equals(exchange.getRequestMethod())) {
                handleGetConsultasPorPaciente(exchange, pacienteId);
            } else {
                apiServer.sendJsonResponse(exchange, 405, "{\"error\": \"Método não permitido\"}");
            }

        } else {
            apiServer.sendJsonResponse(exchange, 404, "{\"error\": \"Endpoint não encontrado\"}");
        }
    }

    private void handleGetConsultas(HttpExchange exchange) throws IOException {
        try {
            List<Consulta> consultas = consultaService.listarTodasConsultas();
            apiServer.sendJsonResponse(exchange, 200, gson.toJson(consultas));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar disponibilidade", e);
            String errorResponse = gson.toJson(new ErrorResponse("Erro ao listar consultas: " + e.getMessage()));
            apiServer.sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    private void handlePostConsulta(HttpExchange exchange) throws IOException {
        try {
            String requestBody = ApiServer.readRequestBody(exchange);
            Consulta consulta = gson.fromJson(requestBody, Consulta.class);

            boolean success = consultaService.agendarConsulta(consulta);

            if (success) {
                String response = gson.toJson(new SimpleResponse(true, "Consulta agendada com sucesso"));
                apiServer.sendJsonResponse(exchange, 201, response);
            } else {
                String response = gson.toJson(new SimpleResponse(false, "Erro ao agendar consulta"));
                apiServer.sendJsonResponse(exchange, 400, response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar disponibilidade", e);
            String errorResponse = gson.toJson(new SimpleResponse(false, "Erro interno: " + e.getMessage()));
            apiServer.sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    private void handlePutConsultaStatus(HttpExchange exchange, Long consultaId) throws IOException {
        try {
            String requestBody = ApiServer.readRequestBody(exchange);
            StatusUpdateRequest statusRequest = gson.fromJson(requestBody, StatusUpdateRequest.class);

            // ❌ PROBLEMA: novoStatus é String, mas você está tentando converter para Long
            // ✅ CORREÇÃO: Mude a classe StatusUpdateRequest para usar Long
            boolean success = consultaService.atualizarStatusConsulta(consultaId, statusRequest.novoStatus);

            if (success) {
                String response = gson.toJson(new SimpleResponse(true, "Status da consulta atualizado"));
                apiServer.sendJsonResponse(exchange, 200, response);
            } else {
                String response = gson.toJson(new SimpleResponse(false, "Erro ao atualizar status"));
                apiServer.sendJsonResponse(exchange, 400, response);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar disponibilidade", e);
            String errorResponse = gson.toJson(new SimpleResponse(false, "Erro interno: " + e.getMessage()));
            apiServer.sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    private void handleGetConsultasPorPaciente(HttpExchange exchange, Long pacienteId) throws IOException {
        try {
            List<Consulta> consultas = consultaService.buscarConsultasPorPaciente(pacienteId);
            apiServer.sendJsonResponse(exchange, 200, gson.toJson(consultas));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar disponibilidade", e);
            String errorResponse = gson.toJson(new ErrorResponse("Erro ao buscar consultas do paciente: " + e.getMessage()));
            apiServer.sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    private Long extractConsultaIdFromPath(String path) {
        try {
            // /consultas/123/status -> 123
            String[] parts = path.split("/");
            for (int i = 0; i < parts.length; i++) {
                if ("consultas".equals(parts[i]) && i + 1 < parts.length) {
                    return Long.parseLong(parts[i + 1]);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Long extractPacienteIdFromPath(String path) {
        try {
            // /consultas/paciente/123 -> 123
            String[] parts = path.split("/");
            for (int i = 0; i < parts.length; i++) {
                if ("paciente".equals(parts[i]) && i + 1 < parts.length) {
                    return Long.parseLong(parts[i + 1]);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // Classes para requests/responses
    private static class StatusUpdateRequest {
        public Long novoStatus;
    }

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