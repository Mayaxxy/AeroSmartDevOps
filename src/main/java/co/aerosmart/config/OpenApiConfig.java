package co.aerosmart.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API.
 * Define información general de la API y esquema de seguridad JWT.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "AeroSmart API",
        version = "1.0",
        description = "API REST para el sistema de gestión de vuelos y pasajeros AeroSmart. " +
                      "Permite gestionar vuelos, pasajeros, check-in, boarding passes y notificaciones.",
        contact = @Contact(
            name = "AeroSmart Team",
            email = "support@aerosmart.co"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Servidor de desarrollo"),
        @Server(url = "https://api.aerosmart.co", description = "Servidor de producción")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Autenticación mediante JWT token. Obtén el token usando el endpoint /api/auth/login"
)
public class OpenApiConfig {
}
