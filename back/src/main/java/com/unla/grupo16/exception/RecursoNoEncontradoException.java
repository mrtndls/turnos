package com.unla.grupo16.exception;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public RecursoNoEncontradoException() {
        super("Error recurso no encontrado");
    }
}
