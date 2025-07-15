package br.edu.ufersa.cc.pd.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MqConnectionData implements Serializable {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public MqConnectionData() {
        host = System.getenv("MQ_HOST");
        port = Integer.parseInt(System.getenv("MQ_PORT"));
        username = System.getenv("MQ_USERNAME");
        password = System.getenv("MQ_PASSWORD");
    }

}
