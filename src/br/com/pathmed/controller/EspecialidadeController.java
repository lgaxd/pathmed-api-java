package br.com.pathmed.controller;

import br.com.pathmed.service.EspecialidadeService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class EspecialidadeController {
    private EspecialidadeService especialidadeService;
    private ApiServer apiServer;
    private Gson gson;

    public EspecialidadeController(ApiServer apiServer) {
        this.especialidadeService = new EspecialidadeService();
        this.apiServer = apiServer;
        this.gson = apiServer.getGson();
    }

    public void handleEspecialidades(HttpExchange exchange) throws IOException {

        if ("GET".equals(exchange.getRequestMethod())) {
            handleGetEspecialidades(exchange);
        } else {
            apiServer.sendJsonResponse(exchange, 405, "{\"error\": \"Método não permitido\"}");
        }
    }

    private void handleGetEspecialidades(HttpExchange exchange) throws IOException {
        try {
            var especialidades = especialidadeService.listarTodasEspecialidades();
            apiServer.sendJsonResponse(exchange, 200, gson.toJson(especialidades));
        } catch (Exception e) {
            String errorResponse = gson.toJson(new ErrorResponse("Erro ao listar especialidades: " + e.getMessage()));
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