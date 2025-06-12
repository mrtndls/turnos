package com.unla.grupo16.exception;

public class ClienteDuplicadoException extends NegocioException {
    public ClienteDuplicadoException(String dni) {
        super("Ya existe un cliente con DNI " + dni);
    }
}