package br.edu.ufersa.cc.pd.exceptions;

public class MqConnectionException extends RuntimeException {

    public MqConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqConnectionException(String message) {
        super(message);
    }

    public MqConnectionException(Throwable cause) {
        super(cause);
    }

}
