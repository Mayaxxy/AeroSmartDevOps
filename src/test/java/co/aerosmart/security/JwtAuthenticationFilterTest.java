package co.aerosmart.security;

import co.aerosmart.services.PassengerService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for JwtAuthenticationFilter to verify role extraction and validation.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PassengerService passengerService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtUtil, passengerService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testValidRoleADMIN() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String email = "admin@test.com";
        String role = "ADMIN";

        when(request.getRequestURI()).thenReturn("/api/admin/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(jwtUtil.validateToken(token, email)).thenReturn(true);
        when(jwtUtil.extractRole(token)).thenReturn(role);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testValidRolePASSENGER() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String email = "passenger@test.com";
        String role = "PASSENGER";

        when(request.getRequestURI()).thenReturn("/api/passenger/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(jwtUtil.validateToken(token, email)).thenReturn(true);
        when(jwtUtil.extractRole(token)).thenReturn(role);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_PASSENGER")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testValidRoleRECEPCIONISTA() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String email = "receptionist@test.com";
        String role = "RECEPCIONISTA";

        when(request.getRequestURI()).thenReturn("/api/receptionist/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(jwtUtil.validateToken(token, email)).thenReturn(true);
        when(jwtUtil.extractRole(token)).thenReturn(role);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_RECEPCIONISTA")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testInvalidRole() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String email = "user@test.com";
        String invalidRole = "INVALID_ROLE";

        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(jwtUtil.validateToken(token, email)).thenReturn(true);
        when(jwtUtil.extractRole(token)).thenReturn(invalidRole);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testNullRole() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String email = "user@test.com";

        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(jwtUtil.validateToken(token, email)).thenReturn(true);
        when(jwtUtil.extractRole(token)).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testEmptyRole() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        String email = "user@test.com";

        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractEmail(token)).thenReturn(email);
        when(jwtUtil.validateToken(token, email)).thenReturn(true);
        when(jwtUtil.extractRole(token)).thenReturn("");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testPublicEndpointSkipsAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractEmail(anyString());
    }
}
