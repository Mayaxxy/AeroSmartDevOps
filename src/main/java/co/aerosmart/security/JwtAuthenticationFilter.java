package co.aerosmart.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import co.aerosmart.services.PassengerService;

import java.io.IOException;

/**
 * Filtro para autenticación JWT en cada request.
 * Extrae y valida el token del header Authorization.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final PassengerService passengerService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, @Lazy PassengerService passengerService) {
        this.jwtUtil = jwtUtil;
        this.passengerService = passengerService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Skip JWT processing for public endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/") || 
            path.startsWith("/api/flights/public/") || 
            path.equals("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // Extraer token del header Authorization
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.extractEmail(jwt);
                logger.info("Email extraído del token: " + email);
            } catch (Exception e) {
                logger.error("Error extrayendo email del token: " + e.getMessage());
            }
        } else {
            logger.debug("No Authorization header found or invalid format");
        }

        // Validar token y establecer autenticación
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtUtil.validateToken(jwt, email)) {
                    logger.info("Token válido para: " + email);
                    
                    // Extract role from token instead of database query
                    String role = jwtUtil.extractRole(jwt);
                    if (role == null || role.isEmpty()) {
                        logger.warn("Token sin rol para: " + email + ", usando USER por defecto");
                        role = "USER"; // Default role if not present in token
                    }
                    
                    logger.info("Rol extraído del token: " + role);
                    
                    // Create UserDetails directly from token claims without database query
                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(email)
                        .password("") // Password not needed for JWT authentication
                        .authorities("ROLE_" + role)
                        .build();
                    
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Autenticación establecida para: " + email + " con rol: ROLE_" + role);
                } else {
                    logger.warn("Token inválido para: " + email);
                }
            } catch (Exception e) {
                logger.error("Error validando token JWT: " + e.getMessage(), e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
