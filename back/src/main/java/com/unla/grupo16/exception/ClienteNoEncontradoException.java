package com.unla.grupo16.exception;

public class ClienteNoEncontradoException extends NegocioException {
    public ClienteNoEncontradoException(Long id) {
        super("No se encontro el cliente con ID " + id);
    }
}