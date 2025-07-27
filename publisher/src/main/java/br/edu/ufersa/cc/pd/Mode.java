package br.edu.ufersa.cc.pd;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Mode {

    REAL_TIME("publisher.real_time"),
    ON_DEMAND("publisher.on_demand");

    private final String queueName;

}
