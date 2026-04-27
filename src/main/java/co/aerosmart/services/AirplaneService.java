package co.aerosmart.services;

import co.aerosmart.model.Airplane;
import co.aerosmart.model.AirplaneStatus;
import co.aerosmart.repository.AirplaneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar aviones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AirplaneService {

    private final AirplaneRepository airplaneRepository;

    /**
     * Obtiene todos los aviones.
     */
    public List<Airplane> getAllAirplanes() {
        return airplaneRepository.findAll();
    }

    /**
     * Obtiene todos los aviones disponibles.
     */
    public List<Airplane> getAvailableAirplanes() {
        return airplaneRepository.findByStatusOrderByRegistrationAsc(AirplaneStatus.AVAILABLE);
    }

    /**
     * Obtiene un avión por su ID.
     */
    public Airplane getAirplaneById(Long id) {
        return airplaneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avión no encontrado con ID: " + id));
    }

    /**
     * Obtiene un avión por su matrícula.
     */
    public Airplane getAirplaneByRegistration(String registration) {
        return airplaneRepository.findByRegistration(registration)
                .orElseThrow(() -> new RuntimeException("Avión no encontrado con matrícula: " + registration));
    }

    /**
     * Crea un nuevo avión.
     */
    @Transactional
    public Airplane createAirplane(Airplane airplane) {
        // Validar que la matrícula no exista
        if (airplaneRepository.existsByRegistration(airplane.getRegistration())) {
            throw new RuntimeException("Ya existe un avión con la matrícula: " + airplane.getRegistration());
        }

        // Normalizar matrícula a mayúsculas
        airplane.setRegistration(airplane.getRegistration().toUpperCase().trim());
        
        log.info("Creando avión con matrícula: {}", airplane.getRegistration());
        return airplaneRepository.save(airplane);
    }

    /**
     * Actualiza un avión existente.
     */
    @Transactional
    public Airplane updateAirplane(Long id, Airplane airplaneDetails) {
        Airplane airplane = getAirplaneById(id);
        
        airplane.setModel(airplaneDetails.getModel());
        airplane.setManufacturer(airplaneDetails.getManufacturer());
        airplane.setCapacity(airplaneDetails.getCapacity());
        airplane.setStatus(airplaneDetails.getStatus());
        airplane.setYearManufactured(airplaneDetails.getYearManufactured());
        
        log.info("Actualizando avión: {}", airplane.getRegistration());
        return airplaneRepository.save(airplane);
    }

    /**
     * Actualiza el estado de un avión.
     */
    @Transactional
    public Airplane updateAirplaneStatus(Long id, AirplaneStatus status) {
        Airplane airplane = getAirplaneById(id);
        airplane.setStatus(status);
        
        log.info("Actualizando estado del avión {} a: {}", airplane.getRegistration(), status);
        return airplaneRepository.save(airplane);
    }

    /**
     * Elimina un avión.
     */
    @Transactional
    public void deleteAirplane(Long id) {
        Airplane airplane = getAirplaneById(id);
        
        // Verificar que no tenga vuelos asignados
        if (!airplane.getFlights().isEmpty()) {
            throw new RuntimeException("No se puede eliminar el avión porque tiene vuelos asignados");
        }
        
        log.info("Eliminando avión: {}", airplane.getRegistration());
        airplaneRepository.delete(airplane);
    }
}
