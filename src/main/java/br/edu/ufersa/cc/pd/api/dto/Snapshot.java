package br.edu.ufersa.cc.pd.api.dto;

import java.util.List;

import br.edu.ufersa.cc.pd.api.apps.Drone.DataFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Snapshot {

    private final double pressure;
    private final double radiation;
    private final double temperature;
    private final double humidity;

    public List<Double> getAsList() {
        return List.of(pressure, radiation, temperature, pressure);
    }

    public String format(final DataFormat format) {
        final var inner = String.join(format.getDelimiter(),
                getAsList().stream().map(data -> data.toString()).toList());

        return new StringBuilder()
                .append(format.getBefore())
                .append(inner)
                .append(format.getAfter())
                .toString();
    }

}
