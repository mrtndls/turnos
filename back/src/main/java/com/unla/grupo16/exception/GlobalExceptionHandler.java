package com.unla.grupo16.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String manejarExceptionGeneral(Exception ex, Model model) {
        logger.error("Error inesperado", ex);
        model.addAttribute("error", "Ha ocurrido un error inesperado");
        return "error/500";
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String manejarRecursoNoEncontrado(RecursoNoEncontradoException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404"; 
    }

    @ExceptionHandler(BaseDeDatosException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String manejarBaseDeDatos(BaseDeDatosException ex, Model model) {
        model.addAttribute("error", "Error en base de datos: " + ex.getMessage());
        return "error/500";
    }

    @ExceptionHandler(ErrorInternoException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String manejarErrorInterno(ErrorInternoException ex, Model model) {
        model.addAttribute("error", "Error interno: " + ex.getMessage());
        return "error/500";
    }

    @ExceptionHandler(NegocioException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String manejarErrorNegocio(NegocioException ex, Model model) {
        model.addAttribute("error", "Error de negocio: " + ex.getMessage());
        return "error/400"; 
    }

    @ExceptionHandler(ClienteNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String manejarClienteNoEncontrado(ClienteNoEncontradoException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(ClienteDuplicadoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String manejarClienteDuplicado(ClienteDuplicadoException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/400"; 
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String manejarIdNulo(InvalidDataAccessApiUsageException ex, Model model) {
        model.addAttribute("error", "ID invalido o nulo: " + ex.getMessage());
        return "error/400";
    }

}
