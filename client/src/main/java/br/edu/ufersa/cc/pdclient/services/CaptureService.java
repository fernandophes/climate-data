package br.edu.ufersa.cc.pdclient.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufersa.cc.pdclient.App;
import br.edu.ufersa.cc.pdclient.dto.CaptureDto;
import br.edu.ufersa.cc.pdclient.entities.Capture;
import br.edu.ufersa.cc.pdclient.repositories.CaptureRepository;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CaptureService {

    private static final Logger LOG = LoggerFactory.getLogger(CaptureService.class.getSimpleName());

    private CaptureRepository captureRepository = new CaptureRepository();

    public List<CaptureDto> listAll() {
        LOG.info("Listando todas as capturas...");
        return captureRepository.listAll().stream()
                .map(capture -> CaptureDto.from(capture.getRegion(), capture.getWeatherData(), App.FORMAT))
                .toList();
    }

    public List<CaptureDto> listByRegion(final String region) {
        LOG.info("Listando capturas da região: {}", region);
        return captureRepository.listByRegion(region).stream()
                .map(capture -> CaptureDto.from(capture.getRegion(), capture.getWeatherData(), App.FORMAT))
                .toList();
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
