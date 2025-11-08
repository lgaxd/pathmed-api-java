package br.com.pathmed.controller;

import br.com.pathmed.model.LoginRequest;
import br.com.pathmed.model.LoginResponse;
import br.com.pathmed.model.RegistroPacienteRequest;
import br.com.pathmed.service.AuthService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthController {
    private AuthService authService;
    private ApiServer apiServer;
    private Gson gson;
    private static final Logger logger = Logger.getLogger(AgendaController.class.getName());

    public AuthController(ApiServer apiServer) {
        this.authService = new AuthService();
        this.apiServer = apiServer;
        this.gson = apiServer.getGson();
    }

    public void handleLogin(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            apiServer.sendJsonResponse(exchange, 405, "{\"error\": \"Método não permitido\"}");
            return;
        }

        try {
            String requestBody = ApiServer.readRequestBody(exchange);
            LoginRequest loginRequest = gson.fromJson(requestBody, LoginRequest.class);

            LoginResponse loginResponse = authService.login(loginRequest);

            if (loginResponse.isSucesso()) {
                apiServer.sendJsonResponse(exchange, 200, gson.toJson(loginResponse));
            } else {
                apiServer.sendJsonResponse(exchange, 401, gson.toJson(loginResponse));
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar disponibilidade", e);
            String errorResponse = gson.toJson(new ErrorResponse("Erro interno no servidor: " + e.getMessage()));
            apiServer.sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    public void handleRegister(HttpExchange exchange) throws IOException {
        System.out.println("handleRegister chamado");

        if (!"POST".equals(exchange.getRequestMethod())) {
            apiServer.sendJsonResponse(exchange, 405, "{\"error\": \"Método não permitido\"}");
            return; // Importante retornar após enviar resposta
        }

        try {
            // Ler o corpo da requisição
            String requestBody = ApiServer.readRequestBody(exchange);

            // Converter para objeto
            RegistroPacienteRequest request = gson.fromJson(requestBody, RegistroPacienteRequest.class);

            // Processar o registro
            LoginResponse response = authService.registrarPaciente(request);

            // Enviar resposta baseada no resultado
            if (response.isSucesso()) {
                apiServer.sendJsonResponse(exchange, 201, gson.toJson(response));
            } else {
                apiServer.sendJsonResponse(exchange, 400, gson.toJson(response));
            }

        } catch (JsonSyntaxException e) {
            // Erro de parse do JSON
            apiServer.sendJsonResponse(exchange, 400, "{\"error\": \"JSON inválido: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            // Outros erros
            logger.log(Level.SEVERE, "Erro ao buscar disponibilidade", e);
            String errorResponse = gson.toJson(new ErrorResponse("Erro no registro: " + e.getMessage()));
            apiServer.sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    private static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }

    // Classes para respostas
    private static class SimpleResponse {
        private boolean success;
        private String message;

        public SimpleResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}