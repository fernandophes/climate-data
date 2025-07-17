package br.edu.ufersa.cc.pd.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import br.edu.ufersa.cc.pd.OperationException;
import br.edu.ufersa.cc.pd.entities.Capture;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CaptureRepositoryOld {

    private static final String URL = buildDatabaseUrl();
    // Real values from environment variables:
    private static final String USER = System.getenv("POSTGRES_USER");
    private static final String PASSWORD = System.getenv("POSTGRES_PASSWORD");

    // Mock data matching docker-compose.yml variables:
    // private static final String USER = "climate_data";
    // private static final String PASSWORD = "climate_data";
    private final String tableName;

    // Configurar acesso ao Banco de Dados
    private static Connection connection = null;

    private static String buildDatabaseUrl() {
        // Real values from environment variables:
        final var pgHost = System.getenv("POSTGRES_HOST");
        final var pgPort = System.getenv("POSTGRES_PORT");
        final var pgDatabase = System.getenv("POSTGRES_DB");

        // System.out.println("DEBUG - POSTGRES_HOST: " + pgHost);
        // System.out.println("DEBUG - POSTGRES_PORT: " + pgPort);
        // System.out.println("DEBUG - POSTGRES_DB: " + pgDatabase);

        // Mock data matching docker-compose.yml variables:
        // final var pgHost = "192.168.0.3";
        // final var pgPort = "5432";
        // final var pgDatabase = "climate_data";

        if (pgHost != null && pgPort != null && pgDatabase != null) {
            // Use PostgreSQL connection for Docker environment
            return String.format("jdbc:postgresql://%s:%s/%s", pgHost, pgPort, pgDatabase);
        } else {
            throw new IllegalStateException("PostgreSQL environment variables are not set.");
        }
    }

    // Abrir uma conexão única, e retornar a atual se já existir
    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (final SQLException e) {
                throw new SQLException("Erro ao abrir conexão com Banco de Dados PostgreSQL", e);
            }
        }
        return connection;
    }

    // Fechar a conexão, caso ela exista
    public static void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    public List<Capture> listAll() {
        try (final var statement = getConnection().createStatement()) {
            final var resultSet = statement
                    .executeQuery(
                            "select id, region, weather_data from " + tableName
                                    + " order by id desc");

            final var result = new ArrayList<Capture>();
            while (resultSet.next()) {
                final var capture = new Capture()
                        .setId(UUID.fromString(resultSet.getString("id")))
                        .setRegion(resultSet.getString("region"))
                        .setWeatherData(resultSet.getString("weather_data"));

                result.add(capture);
            }

            return result;
        } catch (final SQLException e) {
            throw new OperationException("Erro ao listar ordens", e);
        }
    }

    public Capture findById(final UUID id) throws NoSuchElementException {
        final var sql = "select id, region, weather_data from " + tableName + " where id = ?";
        try (final var statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, id.toString());
            final var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Capture()
                        .setId(UUID.fromString(resultSet.getString("id")))
                        .setRegion(resultSet.getString("region"))
                        .setWeatherData(resultSet.getString("weather_data"));
            } else {
                throw new NoSuchElementException();
            }
        } catch (final SQLException e) {
            throw new OperationException("Erro ao consultar ordem", e);
        }
    }

    public void create(final Capture capture) {
        final var sql = "insert into " + tableName + " (weather_data, region) values (?, ?)";

        try (final var statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, capture.getWeatherData());
            statement.setString(2, capture.getRegion());

            if (statement.executeUpdate() == 0) {
                throw new SQLException("Não foi possível cadastrar essa ordem");
            }
        } catch (final SQLException e) {
            throw new OperationException("Erro ao salvar ordem", e);
        }
    }

    public void update(final Capture capture) {
        final var sql = "update " + tableName + " set weather_data = ?, region = ? where id = ?";

        try (final var statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, capture.getWeatherData());
            statement.setString(2, capture.getRegion());
            statement.setString(3, capture.getId().toString());

            if (statement.executeUpdate() == 0) {
                throw new SQLException("Não foi possível atualizar essa ordem");
            }
        } catch (final SQLException e) {
            throw new OperationException("Erro ao atualizar ordem", e);
        }
    }

    public void delete(final Capture capture) {
        final var sql = "delete from " + tableName + " where id = ?";

        try (final var statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, capture.getId().toString());

            if (statement.executeUpdate() == 0) {
                throw new SQLException("Não foi possível excluir essa ordem");
            }
        } catch (final SQLException e) {
            throw new OperationException("Erro ao excluir ordem", e);
        }
    }

    public Long countAll() {
        try (final var statement = getConnection().createStatement()) {
            final var resultSet = statement.executeQuery("select count(*) from " + tableName + "");
            resultSet.next();
            return (long) resultSet.getInt(1);
        } catch (final SQLException e) {
            throw new OperationException("Erro ao contar ordens", e);
        }
    }

}
