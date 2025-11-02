package br.com.pathmed.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class GsonConfig {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Optional.class, new OptionalAdapter())
                .setPrettyPrinting()
                .create();
    }

    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(DATE_FORMATTER));
            }
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String dateStr = in.nextString();
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        }
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(DATE_TIME_FORMATTER));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String dateTimeStr = in.nextString();
            return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        }
    }

    private static class OptionalAdapter extends TypeAdapter<Optional<?>> {
        @Override
        public void write(JsonWriter out, Optional<?> value) throws IOException {
            if (value == null || !value.isPresent()) {
                out.nullValue();
            } else {
                // Serializa o valor contido no Optional
                Object actualValue = value.get();
                if (actualValue == null) {
                    out.nullValue();
                } else {
                    // Usa o Gson padrão para serializar o valor interno
                    Gson gson = new Gson();
                    gson.toJson(actualValue, actualValue.getClass(), out);
                }
            }
        }

        @Override
        public Optional<?> read(JsonReader in) throws IOException {
            // Para leitura, trata como um valor normal e encapsula em Optional
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return Optional.empty();
            }
            // Como não sabemos o tipo exato, retornamos Optional.empty()
            // Ou você pode implementar lógica mais específica se necessário
            in.skipValue();
            return Optional.empty();
        }
    }
}