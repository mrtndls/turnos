package com.unla.grupo16.exception;

public class ErrorInternoException extends RuntimeException {
    public ErrorInternoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}