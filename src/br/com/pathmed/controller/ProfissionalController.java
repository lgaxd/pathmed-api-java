package br.com.pathmed.controller;

import br.com.pathmed.service.ProfissionalService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class ProfissionalController {
    private ProfissionalService profissionalService;
    private ApiServer apiServer;
    private Gson gson;

    public ProfissionalController(ApiServer apiServer) {
        this.profissionalService = new ProfissionalService();
        this.apiServer = apiServer;
        this.gson = apiServer.getGson();
    }

    public void handleProfissionais(HttpExchange exchange) throws IOException {
        apiServer.setupCorsHeaders(exchange);

        if ("GET".equals(exchange.getRequestMethod())) {
            handleGetProfissionais(exchange);
        } else {
            apiServer.sendJsonResponse(exchange, 405, "{\"error\": \"Método não permitido\"}");
        }
    }

    private void handleGetProfissionais(HttpExchange exchange) throws IOException {
        try {
            var profissionais = profissionalService.listarTodosProfissionais();
            apiServer.sendJsonResponse(exchange, 200, gson.toJson(profissionais));
        } catch (Exception e) {
            String errorResponse = gson.toJson(new ErrorResponse("Erro ao listar profissionais: " + e.getMessage()));
            apiServer.sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    private static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}