package com.unla.grupo16.exception;

public class BaseDeDatosException extends RuntimeException {

    public BaseDeDatosException(String mensaje) {
        super(mensaje);
    }

    public BaseDeDatosException() {
        super("Error base de datos");
    }
}
