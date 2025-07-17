package br.edu.ufersa.cc.pd.services;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.entities.Capture;
import br.edu.ufersa.cc.pd.repositories.CaptureRepository;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CaptureService {

    private static final Logger LOG = LoggerFactory.getLogger(CaptureService.class.getSimpleName());

    private CaptureRepository captureRepository;

    public void initialize() throws SQLException {
        final var tableName = "captures";

        captureRepository = new CaptureRepository(tableName);

        try (final var statement = CaptureRepository.getConnection().createStatement()) {
            statement.executeUpdate(
                    "create table if not exists " + tableName
                            + " (id UUID DEFAULT RANDOM_UUID() PRIMARY KEY, weather_data TEXT, region VARCHAR(255))");
            LOG.info("Criada tabela {}", tableName);
        } catch (SQLException e) {
            throw new SQLException("Erro ao inicializar banco de dados", e);
        }
    }

    public Long countAll() {
        return captureRepository.countAll();
    }

    public List<Capture> listAll() {
        LOG.info("Listando todas as ordens...");
        return captureRepository.listAll();
    }

    public void create(final Capture capture) {
        captureRepository.create(capture);
        LOG.info("Ordem cadastrada");
    }

    public Capture findById(final UUID id) throws NoSuchElementException {
        LOG.info("Buscando ordem...");
        return captureRepository.findById(id);
    }

    public void update(final Capture capture) {
        captureRepository.update(capture);
        LOG.info("Ordem atualizada");
    }

    public void delete(final Capture capture) {
        captureRepository.delete(capture);
        LOG.info("Ordem excluída");
    }

}
