package com.unla.grupo16.exception;

public class BaseDeDatosException extends RuntimeException {
    public BaseDeDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}