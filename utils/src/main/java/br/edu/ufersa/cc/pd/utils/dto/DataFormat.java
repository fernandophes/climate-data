package br.edu.ufersa.cc.pd.utils.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataFormat implements Serializable {

    private String delimiter;
    private String start;
    private String end;

    public DataFormat(final String delimiter) {
        this.delimiter = delimiter;
        start = "";
        end = "";
    }

}
