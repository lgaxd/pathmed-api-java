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

        // Configurar rotas
        setupRoutes();

        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    /**
     * Envelopa um HttpHandler com a lógica de CORS.
     * 1. Define os cabeçalhos CORS.
     * 2. Responde a requisições OPTIONS (preflight) com 204 No Content.
     * 3. Delega para o handler original para requisições reais (GET, POST, etc.).
     */
    private HttpHandler wrapHandlerWithCors(HttpHandler originalHandler) {
        return exchange -> {
            // 1. Configura os cabeçalhos CORS para todas as respostas
            setupCorsHeaders(exchange);

            // 2. Lida com a requisição OPTIONS (preflight) imediatamente
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // 204 No Content (tamanho -1 para indicar que não há corpo)
                exchange.close();
                return;
            }

            // 3. Delega para o handler original para requisições de dados
            originalHandler.handle(exchange);
        };
    }

    private void setupRoutes() {
        // Criar instâncias dos controllers com injeção do Gson (assumindo que estas classes existem)
        AuthController authController = new AuthController(this);
        PacienteController pacienteController = new PacienteController(this);
        ConsultaController consultaController = new ConsultaController(this);
        ProfissionalController profissionalController = new ProfissionalController(this);
        EspecialidadeController especialidadeController = new EspecialidadeController(this);
        AgendaController agendaController = new AgendaController(this);

        // Auth routes - TODOS ENVELOPADOS COM CORS
        server.createContext("/auth/login", wrapHandlerWithCors(authController::handleLogin));
        server.createContext("/auth/pacientes/register", wrapHandlerWithCors(authController::handleRegister));

        // Paciente routes - TODOS ENVELOPADOS COM CORS
        server.createContext("/pacientes", wrapHandlerWithCors(pacienteController::handlePacientes));
        server.createContext("/pacientes/", wrapHandlerWithCors(pacienteController::handlePacienteById));

        // Consulta routes - TODOS ENVELOPADOS COM CORS
        server.createContext("/consultas", wrapHandlerWithCors(consultaController::handleConsultas));
        server.createContext("/consultas/", wrapHandlerWithCors(consultaController::handleConsultaActions));

        // Profissional routes - TODOS ENVELOPADOS COM CORS
        server.createContext("/profissionais", wrapHandlerWithCors(profissionalController::handleProfissionais));

        // Especialidade routes - TODOS ENVELOPADOS COM CORS
        server.createContext("/especialidades", wrapHandlerWithCors(especialidadeController::handleEspecialidades));

        // Agenda routes - TODOS ENVELOPADOS COM CORS
        server.createContext("/agenda/disponibilidade", wrapHandlerWithCors(agendaController::handleDisponibilidade));

        // Não é mais necessário um CorsHandler genérico, pois as rotas específicas estão
        // sendo envelopadas.
    }

    // Método utilitário para configurar CORS (mantido)
    public void setupCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
    }

    // Método utilitário para enviar respostas JSON (mantido)
    public void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonResponse.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

    // Método utilitário para ler o corpo da requisição (mantido)
    public static String readRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes());
    }

    // Método utilitário para extrair ID da URL (mantido)
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