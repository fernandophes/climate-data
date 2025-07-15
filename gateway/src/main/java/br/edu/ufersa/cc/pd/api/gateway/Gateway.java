// package br.edu.ufersa.cc.pd.api.gateway;

// import br.edu.ufersa.cc.pd.api.amqp.MqttConnection;
// import br.edu.ufersa.cc.pd.api.database.entities.Weather;
// import br.edu.ufersa.cc.pd.api.database.services.WeatherService;
// import com.google.gson.JsonObject;
// import com.google.gson.JsonParser;

// import java.io.Closeable;
// import java.io.IOException;
// import java.net.InetSocketAddress;
// import java.sql.SQLException;
// import java.util.Arrays;
// import java.util.List;

// public class Gateway implements Runnable, Closeable {
//     private String EXCHANGE = "drones";
//     private String EXCHANGE_TYPE = "fanout";
//     private final MqttConnection mqttConnection;

//     public Gateway(InetSocketAddress host, int port) {
//         super();
//         this.mqttConnection = new MqttConnection<String>(this.EXCHANGE, this.EXCHANGE_TYPE);
//     }

//     @Override
//     public void run() {
//         this.mqttConnection.readMessage();
//     }

//     @Override
//     public void close() throws IOException {

//     }

//     public static void processData(String data) {
//         final var message = JsonParser.parseString(data).getAsJsonObject();
//         saveDataInDatabase(message);
//     }

//     private static void saveDataInDatabase(final JsonObject data) {
//         final WeatherService weatherService = new WeatherService();

//         final var climateData = data.get("data").getAsString();
//         final var region = data.get("drone").getAsString();

//         final List<String> originalData = Arrays.asList(climateData.replace("[", "").replace("]", "").split(", "));

//         originalData.forEach(item -> {
//             final var weather = new Weather();
//             weather.setWeatherData(item.replaceAll("[,#;\\-]", "|"));
//             weather.setRegion(region);

//             try {
//                 weatherService.add(weather);
//             } catch (final SQLException e) {
//                 throw new RuntimeException(e);
//             }
//         });
//     }
// }
