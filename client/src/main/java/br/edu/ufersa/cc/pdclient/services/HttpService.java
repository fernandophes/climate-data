package br.edu.ufersa.cc.pdclient.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.utils.JsonUtils;
import br.edu.ufersa.cc.pd.utils.dto.DroneMessage;
import br.edu.ufersa.cc.pdclient.App;

public class HttpService {

    private static final Logger LOG = LoggerFactory.getLogger(HttpService.class.getSimpleName());
    private static final String PATH = "/climate-data";

    private final String host = App.getHost();
    private final int port = Integer.parseInt(System.getenv("HTTP_PORT"));
    private final HttpClient client = HttpClient.newHttpClient();

    public List<DroneMessage> get() {
        final var endpoint = "http://" + host + ":" + port + PATH;
        LOG.info("Endpoint: \n{}", endpoint);

        final var uri = URI.create(endpoint);
        final var request = HttpRequest.newBuilder(uri)
                .GET()
                .build();

        try {
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            final var json = response.body();
            final var array = JsonUtils.fromJson(json, DroneMessage[].class);

            return List.of(array);
        } catch (IOException | InterruptedException e) {
            LOG.error("Erro ao ler do endpoint", e);
            return List.of();
        }
    }

}
