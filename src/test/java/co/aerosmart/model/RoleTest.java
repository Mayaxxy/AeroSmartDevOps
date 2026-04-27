package co.aerosmart.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el enum Role.
 * Verifica que los valores del enum sean correctos y que la conversión funcione.
 */
@DisplayName("Role Enum Tests")
class RoleTest {

    @Test
    @DisplayName("Debe contener los tres roles esperados")
    void shouldContainAllExpectedRoles() {
        Role[] roles = Role.values();
        assertEquals(3, roles.length, "Debe haber exactamente 3 roles");
        
        assertTrue(containsRole(roles, Role.ADMIN), "Debe contener ADMIN");
        assertTrue(containsRole(roles, Role.PASSENGER), "Debe contener PASSENGER");
        assertTrue(containsRole(roles, Role.RECEPCIONISTA), "Debe contener RECEPCIONISTA");
    }

    @Test
    @DisplayName("Debe convertir correctamente de String a Role")
    void shouldConvertFromStringToRole() {
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
        assertEquals(Role.PASSENGER, Role.valueOf("PASSENGER"));
        assertEquals(Role.RECEPCIONISTA, Role.valueOf("RECEPCIONISTA"));
    }

    @Test
    @DisplayName("Debe convertir correctamente de Role a String")
    void shouldConvertFromRoleToString() {
        assertEquals("ADMIN", Role.ADMIN.name());
        assertEquals("PASSENGER", Role.PASSENGER.name());
        assertEquals("RECEPCIONISTA", Role.RECEPCIONISTA.name());
    }

    @Test
    @DisplayName("Debe lanzar excepción para rol inválido")
    void shouldThrowExceptionForInvalidRole() {
        assertThrows(IllegalArgumentException.class, () -> {
            Role.valueOf("INVALID_ROLE");
        });
    }

    private boolean containsRole(Role[] roles, Role target) {
        for (Role role : roles) {
            if (role == target) {
                return true;
            }
        }
        return false;
    }
}
