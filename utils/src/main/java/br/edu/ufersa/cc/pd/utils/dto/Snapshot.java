package br.edu.ufersa.cc.pd.utils.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Snapshot implements Serializable {

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
                .append(format.getStart())
                .append(inner)
                .append(format.getEnd())
                .toString();
    }

}
