package br.com.pathmed.controller;

import br.com.pathmed.service.DisponibilidadeService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AgendaController {
    private DisponibilidadeService disponibilidadeService;
    private ApiServer apiServer;
    private Gson gson;

    public AgendaController(ApiServer apiServer) {
        this.disponibilidadeService = new DisponibilidadeService();
        this.apiServer = apiServer;
        this.gson = apiServer.getGson();
    }

    public void handleDisponibilidade(HttpExchange exchange) throws IOException {
        apiServer.setupCorsHeaders(exchange);

        if ("GET".equals(exchange.getRequestMethod())) {
            handleGetDisponibilidade(exchange);
        } else {
            apiServer.sendJsonResponse(exchange, 405, "{\"error\": \"Método não permitido\"}");
        }
    }

    private void handleGetDisponibilidade(HttpExchange exchange) throws IOException {
        try {
            // Extrair parâmetros da query string
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

            // Obter ID da especialidade (obrigatório)
            String especialidadeIdStr = queryParams.get("especialidade");
            if (especialidadeIdStr == null) {
                apiServer.sendJsonResponse(exchange, 400, "{\"error\": \"Parâmetro 'especialidade' é obrigatório\"}");
                return;
            }

            Long idEspecialidade;
            try {
                idEspecialidade = Long.parseLong(especialidadeIdStr);
            } catch (NumberFormatException e) {
                apiServer.sendJsonResponse(exchange, 400, "{\"error\": \"ID da especialidade inválido\"}");
                return;
            }

            // Obter data (opcional - padrão: hoje)
            LocalDate data;
            String dataStr = queryParams.get("data");
            if (dataStr != null) {
                try {
                    data = LocalDate.parse(dataStr);
                } catch (Exception e) {
                    apiServer.sendJsonResponse(exchange, 400, "{\"error\": \"Formato de data inválido. Use YYYY-MM-DD\"}");
                    return;
                }
            } else {
                data = LocalDate.now();
            }

            // Buscar disponibilidade
            var disponibilidade = disponibilidadeService.buscarDisponibilidadeDia(data, idEspecialidade);

            if (disponibilidade != null) {
                // Adicionar relatório resumido à resposta
                Map<String, Object> response = new HashMap<>();
                response.put("disponibilidade", disponibilidade);
                response.put("relatorio", disponibilidadeService.gerarRelatorioDisponibilidade(disponibilidade));

                apiServer.sendJsonResponse(exchange, 200, gson.toJson(response));
            } else {
                apiServer.sendJsonResponse(exchange, 404, "{\"error\": \"Nenhuma disponibilidade encontrada para os parâmetros informados\"}");
            }

        } catch (IllegalArgumentException e) {
            apiServer.sendJsonResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            String errorResponse = gson.toJson(new ErrorResponse("Erro ao buscar disponibilidade: " + e.getMessage()));
            apiServer.sendJsonResponse(exchange, 500, errorResponse);
        }
    }

    /**
     * Método auxiliar para parsear parâmetros de query string
     */
    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    private static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}