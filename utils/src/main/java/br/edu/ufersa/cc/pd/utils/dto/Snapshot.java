package br.edu.ufersa.cc.pd.utils.dto;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Snapshot implements Serializable {

    private double pressure;
    private double radiation;
    private double temperature;
    private double humidity;

    public static Snapshot from(final String formatted, final DataFormat format) {
        final var inner = formatted.replace(format.getStart(), "").replace(format.getEnd(), "");
        final var parts = Stream.of(inner.split(Pattern.quote(format.getDelimiter())))
                .map(Double::parseDouble)
                .toList();

        return new Snapshot(parts.get(0), parts.get(1), parts.get(2), parts.get(3));
    }

    public List<Double> getAsList() {
        return List.of(pressure, radiation, temperature, pressure);
    }

    public String format(final DataFormat format) {
        final var inner = String.join(format.getDelimiter(),
                getAsList().stream().map(Object::toString).toList());

        return new StringBuilder()
                .append(format.getStart())
                .append(inner)
                .append(format.getEnd())
                .toString();
    }

}
