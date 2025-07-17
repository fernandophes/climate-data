package br.edu.ufersa.cc.pd.utils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public interface JsonUtils {

    final Logger LOG = LoggerFactory.getLogger(JsonUtils.class.getSimpleName());
    final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    final ObjectReader READER = MAPPER.reader();
    final ObjectWriter WRITER = MAPPER.writer().withDefaultPrettyPrinter();

    public static String toJson(final Object object) {
        try {
            return WRITER.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            LOG.error("Erro de JSON", e);
            return "";
        }
    }

    public static <T> T fromJson(final String json, final Class<T> type) {
        try {
            return READER.readValue(json, type);
        } catch (final IOException e) {
            LOG.error("Erro de JSON", e);
            return null;
        }
    }

}
