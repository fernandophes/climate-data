package br.edu.ufersa.cc.pd;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Mode {

    REAL_TIME("climate_data.all_real_time"),
    ON_DEMAND("climate_data.all");

    private final String queueName;

}
