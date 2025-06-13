package com.unla.grupo16.configurations.security.jwt;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.unla.grupo16.services.impl.UserServiceImp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserServiceImp userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        System.out.println("[JWT FILTER] Authorization header: " + authHeader);

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("[JWT FILTER] Token recibido: " + jwt);
            try {
                username = jwtUtil.getUsernameFromToken(jwt);
                System.out.println("[JWT FILTER] Username extraído del token: " + username);
            } catch (Exception e) {
                System.out.println("[JWT FILTER] Error al extraer username del token: " + e.getMessage());
            }
        } else {
            System.out.println("[JWT FILTER] Header Authorization ausente o mal formado");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("[JWT FILTER] Cargando UserDetails para: " + username);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                System.out.println("[JWT FILTER] Token validado correctamente");

                // Extraer roles del token, si tienes método
                List<String> roles = null;
                try {
                    roles = jwtUtil.getRolesFromToken(jwt);
                    System.out.println("[JWT FILTER] Roles extraídos del token: " + roles);
                } catch (Exception e) {
                    System.out.println("[JWT FILTER] No se pudo extraer roles del token o método no implementado");
                }

                List<GrantedAuthority> authorities;
                if (roles != null) {
                    authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                } else {
                    // fallback: tomar roles del UserDetails si no hay método getRolesFromToken
                    authorities = userDetails.getAuthorities().stream().collect(Collectors.toList());
                    System.out.println("[JWT FILTER] Roles obtenidos de UserDetails: " + authorities);
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("[JWT FILTER] Autenticación seteada en SecurityContext");
            } else {
                System.out.println("[JWT FILTER] Token inválido o expirado");
            }
        } else {
            if (username == null) {
                System.out.println("[JWT FILTER] Username es null, no se crea contexto de seguridad");
            }
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                System.out.println("[JWT FILTER] Ya existe autenticación en contexto");
            }
        }

        chain.doFilter(request, response);
    }

}
