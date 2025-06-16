package com.unla.grupo16.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejador para AutenticacionException
    @ExceptionHandler(AutenticacionException.class)
    public ResponseEntity<Map<String, Object>> manejarAutenticacionException(AutenticacionException ex) {
        return construirResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // Manejador para UsuarioDeshabilitadoException
    @ExceptionHandler(UsuarioDeshabilitadoException.class)
    public ResponseEntity<Map<String, Object>> manejarUsuarioDeshabilitadoException(UsuarioDeshabilitadoException ex) {
        return construirResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // Manejador para NegocioException (checked exception)
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, Object>> manejarNegocioException(NegocioException ex) {
        return construirResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Manejador para RecursoNoEncontradoException
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return construirResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Manejador para DisponibilidadNoEncontradaException
    @ExceptionHandler(DisponibilidadNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> manejarDisponibilidadNoEncontrada(DisponibilidadNoEncontradaException ex) {
        return construirResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Manejador para ClienteNoEncontradoException
    @ExceptionHandler(ClienteNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarClienteNoEncontrado(ClienteNoEncontradoException ex) {
        return construirResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Manejador para ClienteDuplicadoException
    @ExceptionHandler(ClienteDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> manejarClienteDuplicado(ClienteDuplicadoException ex) {
        return construirResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Manejador para BaseDeDatosException y ErrorInternoException
    @ExceptionHandler({BaseDeDatosException.class, ErrorInternoException.class})
    public ResponseEntity<Map<String, Object>> manejarErrorInterno(RuntimeException ex) {
        // Para no filtrar el mensaje real en producción podrías poner un mensaje genérico
        return construirResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor.");
    }

    // Handler para IllegalArgumentException (por ejemplo en validaciones)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarIllegalArgument(IllegalArgumentException ex) {
        return construirResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Manejador genérico para cualquier otra excepción no capturada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarExcepcionGenerica(Exception ex) {
        return construirResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado.");
    }

    // Método auxiliar para construir la respuesta de error en formato JSON
    private ResponseEntity<Map<String, Object>> construirResponse(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("estado", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        return new ResponseEntity<>(body, status);
    }
}
