package com.unla.grupo16.exception;

public class UsuarioDeshabilitadoException extends NegocioException {

    public UsuarioDeshabilitadoException() {
        super("El usuario está deshabilitado");
    }

    public UsuarioDeshabilitadoException(String mensaje) {
        super(mensaje);
    }

}
