package com.unla.grupo16.exception;

public class ClienteNoEncontradoException extends NegocioException {

    public ClienteNoEncontradoException(String message) {
        super("No se encontro el cliente con ID " + message);
    }

    public ClienteNoEncontradoException() {
        super("Error cliente no encontrado");
    }
}
