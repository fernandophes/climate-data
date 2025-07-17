package br.edu.ufersa.cc.pdclient.dto;

import br.edu.ufersa.cc.pd.utils.dto.DataFormat;
import br.edu.ufersa.cc.pd.utils.dto.Snapshot;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CaptureDto extends Snapshot {

    private final String region;

    public CaptureDto(String region, double pressure, double radiation, double temperature, double humidity) {
        super(pressure, radiation, temperature, humidity);
        this.region = region;
    }

    public static CaptureDto from(String region, String formatted, DataFormat format) {
        final var snapshot = Snapshot.from(formatted, format);
        return new CaptureDto(region, snapshot.getPressure(), snapshot.getRadiation(), snapshot.getTemperature(),
                snapshot.getHumidity());
    }

}
