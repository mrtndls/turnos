package com.unla.grupo16.exception;

public class ClienteDuplicadoException extends NegocioException {

    public ClienteDuplicadoException(String message) {
        super("Ya existe un cliente con DNI " + message);
    }

    public ClienteDuplicadoException() {
        super("Error cliente duplicado");
    }
}
