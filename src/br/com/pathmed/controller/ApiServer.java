package br.com.pathmed.controller;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import br.com.pathmed.util.GsonConfig;
import com.google.gson.Gson;

public class ApiServer {

    private HttpServer server;
    private Gson gson;

    public ApiServer() {
        this.gson = GsonConfig.createGson();
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        // Configurar CORS handler global
        // server.createContext("/", new CorsHandler());

        // Configurar rotas
        setupRoutes();

        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void setupRoutes() {
        // Criar instâncias dos controllers com injeção do Gson
        AuthController authController = new AuthController(this);
        PacienteController pacienteController = new PacienteController(this);
        ConsultaController consultaController = new ConsultaController(this);
        ProfissionalController profissionalController = new ProfissionalController(this);
        EspecialidadeController especialidadeController = new EspecialidadeController(this);
        AgendaController agendaController = new AgendaController(this);

        // Auth routes
        server.createContext("/auth/login", authController::handleLogin);
        server.createContext("/auth/pacientes/register", exchange -> {
            setupCorsHeaders(exchange);
            authController.handleRegister(exchange);
        });

        // Paciente routes
        server.createContext("/pacientes", pacienteController::handlePacientes);
        server.createContext("/pacientes/", pacienteController::handlePacienteById);

        // Consulta routes
        server.createContext("/consultas", consultaController::handleConsultas);
        server.createContext("/consultas/", consultaController::handleConsultaActions);

        // Profissional routes
        server.createContext("/profissionais", profissionalController::handleProfissionais);

        // Especialidade routes
        server.createContext("/especialidades", especialidadeController::handleEspecialidades);

        // Agenda routes
        server.createContext("/agenda/disponibilidade", agendaController::handleDisponibilidade);

        server.createContext("/", new CorsHandler());
    }

    // Handler global para CORS
    private class CorsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setupCorsHeaders(exchange);

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No Content
                return;
            }

            // Se não for OPTIONS, retorna 404 para rotas não mapeadas
            String response = "{\"error\": \"Endpoint não encontrado\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(404, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    // Método utilitário para configurar CORS (agora não estático)
    public void setupCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
    }

    // Método utilitário para enviar respostas JSON (agora não estático)
    public void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonResponse.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

    // Método utilitário para ler o corpo da requisição (mantém estático pois não depende da instância)
    public static String readRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes());
    }

    // Método utilitário para extrair ID da URL (mantém estático)
    public static Long extractIdFromPath(String path) {
        try {
            String[] parts = path.split("/");
            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public Gson getGson() {
        return gson;
    }
}