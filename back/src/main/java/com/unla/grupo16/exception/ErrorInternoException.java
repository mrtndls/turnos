package com.unla.grupo16.exception;

public class ErrorInternoException extends RuntimeException {

    public ErrorInternoException(String mensaje) {
        super(mensaje);
    }

    public ErrorInternoException() {
        super("Error interno");
    }
}
