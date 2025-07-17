package br.edu.ufersa.cc.pd.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pd.entities.Capture;
import br.edu.ufersa.cc.pd.repositories.CaptureRepository;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CaptureService {

    private static final Logger LOG = LoggerFactory.getLogger(CaptureService.class.getSimpleName());

    private CaptureRepository captureRepository = new CaptureRepository();

    public List<Capture> listAll() {
        LOG.info("Listando todas as capturas...");
        return captureRepository.listAll();
    }

    public List<Capture> listByRegion(final String region) {
        LOG.info("Listando capturas da região: {}", region);
        return captureRepository.listByRegion(region);
    }

    public long countAll() {
        LOG.info("Contando todas as capturas...");
        return captureRepository.countAll();
    }

    public void create(final Capture capture) {
        captureRepository.create(capture);
        LOG.info("Captura cadastrada");
    }

}
