package br.edu.ufersa.cc.pd.utils.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DataFormat {

    private final String delimiter;
    private final String start;
    private final String end;

    public DataFormat(final String delimiter) {
        this.delimiter = delimiter;
        start = "";
        end = "";
    }

}
