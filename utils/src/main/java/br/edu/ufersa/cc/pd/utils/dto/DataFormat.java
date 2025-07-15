package br.edu.ufersa.cc.pd.utils.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DataFormat implements Serializable {

    private final String delimiter;
    private final String start;
    private final String end;

    public DataFormat(final String delimiter) {
        this.delimiter = delimiter;
        start = "";
        end = "";
    }

}
