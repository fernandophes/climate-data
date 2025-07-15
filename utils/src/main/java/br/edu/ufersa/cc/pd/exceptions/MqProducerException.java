package br.edu.ufersa.cc.pd.exceptions;

public class MqProducerException extends RuntimeException {

    public MqProducerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MqProducerException(String message) {
        super(message);
    }

    public MqProducerException(Throwable cause) {
        super(cause);
    }

}
