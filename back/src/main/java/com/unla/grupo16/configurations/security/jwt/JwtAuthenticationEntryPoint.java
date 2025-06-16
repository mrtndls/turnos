package com.unla.grupo16.configurations.security.jwt;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "estado", HttpServletResponse.SC_UNAUTHORIZED,
                "error", "Unauthorized",
                "mensaje", "No autorizado o token inválido"
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(mapper.writeValueAsString(body));
    }
}
