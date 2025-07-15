package br.edu.ufersa.cc.pd;

public class OperationException extends RuntimeException {

    public OperationException(final String message) {
        super(message);
    }

    public OperationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public OperationException(final Throwable cause) {
        super(cause);
    }

}
