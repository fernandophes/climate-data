package br.edu.ufersa.cc.pdclient;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Mode {

    REAL_TIME("MQTT_PORT"),
    ON_DEMAND("MQ_PORT");

    private final String portEnv;

}
