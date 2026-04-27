package co.aerosmart.services;

import co.aerosmart.dto.BoardingPassDTO;
import co.aerosmart.mappers.BoardingPassMapper;
import co.aerosmart.model.BoardingPass;
import co.aerosmart.model.CheckIn;
import co.aerosmart.model.Passenger;
import co.aerosmart.repository.BoardingPassRepository;
import co.aerosmart.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio para gestión de pases de abordaje con QR.
 * El QR se genera solo cuando el check-in está habilitado.
 * El QR es dinámico (se regenera cada 60 segundos) y tiene validez limitada.
 * Contiene un token seguro (UUID), no datos personales.
 * Es de uso único.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BoardingPassService {

    private final BoardingPassRepository boardingPassRepository;
    private final QRCodeService qrCodeService;
    private final BoardingPassMapper boardingPassMapper;
    private final PassengerRepository passengerRepository;

    /**
     * Genera un pase de abordaje con QR para un check-in.
     * Si el usuario autenticado es RECEPCIONISTA, registra su ID para auditoría.
     */
    @Transactional
    public BoardingPassDTO generateBoardingPass(CheckIn checkIn) {
        // Validar que el check-in permita generación de QR
        if (!checkIn.allowsQRGeneration()) {
            throw new IllegalStateException("El check-in no permite generación de QR");
        }

        // Validar que el vuelo permita generación de QR
        if (!checkIn.getReservation().getFlight().allowsQRGeneration()) {
            throw new IllegalStateException("El vuelo está cancelado, no se puede generar QR");
        }

        // Generar token seguro (UUID)
        String boardingToken = UUID.randomUUID().toString();

        // Calcular validez del QR (hasta el cierre de abordaje)
        LocalDateTime validUntil = checkIn.getReservation().getFlight().getBoardingCloseTime();

        // Crear pase de abordaje
        BoardingPass boardingPass = new BoardingPass();
        boardingPass.setCheckIn(checkIn);
        boardingPass.setBoardingToken(boardingToken);
        boardingPass.setValidUntil(validUntil);
        
        // Extraer receptionistId si el usuario autenticado es RECEPCIONISTA
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isReceptionist = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_RECEPCIONISTA"));
            
            if (isReceptionist) {
                String receptionistEmail = authentication.getName();
                Passenger receptionist = passengerRepository.findByEmail(receptionistEmail)
                    .orElseThrow(() -> new IllegalStateException("Recepcionista no encontrado"));
                boardingPass.setReceptionistId(receptionist.getId());
                log.info("Pase de abordaje generado por recepcionista ID: {}", receptionist.getId());
            }
        }
        
        boardingPass = boardingPassRepository.save(boardingPass);

        log.info("Pase de abordaje generado para check-in {}", checkIn.getId());

        // Generar QR code
        String qrCodeBase64 = qrCodeService.generateQRCode(boardingToken);

        return boardingPassMapper.toDTO(boardingPass, qrCodeBase64);
    }

    /**
     * Obtiene el pase de abordaje de un pasajero.
     * Si el QR necesita regeneración (cada 60 seg), genera uno nuevo.
     */
    @Transactional
    public BoardingPassDTO getBoardingPass(Long checkInId, String passengerEmail) {
        CheckIn checkIn = boardingPassRepository.findByCheckInId(checkInId)
            .orElseThrow(() -> new IllegalArgumentException("Pase de abordaje no encontrado"))
            .getCheckIn();

        // Validar que el pase pertenezca al pasajero
        if (!checkIn.getReservation().getPassenger().getEmail().equals(passengerEmail)) {
            throw new IllegalArgumentException("Este pase no pertenece al pasajero autenticado");
        }

        BoardingPass boardingPass = boardingPassRepository.findByCheckInId(checkInId)
            .orElseThrow(() -> new IllegalArgumentException("Pase de abordaje no encontrado"));

        // Regenerar token si es necesario (QR dinámico)
        if (boardingPass.needsRegeneration() && boardingPass.isValid()) {
            String newToken = UUID.randomUUID().toString();
            boardingPass.setBoardingToken(newToken);
            boardingPass.setLastRegeneratedAt(LocalDateTime.now());
            boardingPass = boardingPassRepository.save(boardingPass);
            
            log.info("Token de pase de abordaje regenerado para check-in {}", checkInId);
        }

        // Generar QR code
        String qrCodeBase64 = qrCodeService.generateQRCode(boardingPass.getBoardingToken());

        return boardingPassMapper.toDTO(boardingPass, qrCodeBase64);
    }

    /**
     * Obtiene el pase de abordaje por check-in ID sin validar pasajero.
     * Usado por recepcionistas para obtener pases de abordaje de cualquier pasajero.
     * Si el QR necesita regeneración (cada 60 seg), genera uno nuevo.
     */
    @Transactional
    public BoardingPassDTO getBoardingPassByCheckInId(Long checkInId) {
        BoardingPass boardingPass = boardingPassRepository.findByCheckInId(checkInId)
            .orElseThrow(() -> new IllegalArgumentException("Pase de abordaje no encontrado"));

        // Regenerar token si es necesario (QR dinámico)
        if (boardingPass.needsRegeneration() && boardingPass.isValid()) {
            String newToken = UUID.randomUUID().toString();
            boardingPass.setBoardingToken(newToken);
            boardingPass.setLastRegeneratedAt(LocalDateTime.now());
            boardingPass = boardingPassRepository.save(boardingPass);
            
            log.info("Token de pase de abordaje regenerado para check-in {}", checkInId);
        }

        // Generar QR code
        String qrCodeBase64 = qrCodeService.generateQRCode(boardingPass.getBoardingToken());

        return boardingPassMapper.toDTO(boardingPass, qrCodeBase64);
    }

    /**
     * Valida y usa un pase de abordaje al escanear el QR.
     * Verifica:
     * - Que el token exista
     * - Que esté activo (no usado)
     * - Que no haya sido usado antes
     * - Que corresponda al vuelo correcto
     * - Que esté dentro del tiempo válido
     */
    @Transactional
    public void validateAndUseBoardingPass(String boardingToken, String flightCode) {
        BoardingPass boardingPass = boardingPassRepository.findByBoardingToken(boardingToken)
            .orElseThrow(() -> new IllegalArgumentException("Token de abordaje inválido"));

        // Validar que el QR sea válido
        if (!boardingPass.isValid()) {
            if (boardingPass.isUsed()) {
                throw new IllegalStateException("Este pase de abordaje ya fue usado");
            } else {
                throw new IllegalStateException("Este pase de abordaje ha expirado");
            }
        }

        // Validar que corresponda al vuelo correcto
        String passBoardingFlightCode = boardingPass.getCheckIn().getReservation().getFlight().getFlightCode();
        if (!passBoardingFlightCode.equals(flightCode)) {
            throw new IllegalArgumentException("Este pase no corresponde al vuelo " + flightCode);
        }

        // Validar que el boarding esté abierto
        if (!boardingPass.getCheckIn().getReservation().getFlight().isBoardingOpen()) {
            throw new IllegalStateException("El abordaje no está abierto para este vuelo");
        }

        // Marcar como usado
        boardingPass.markAsUsed();
        boardingPassRepository.save(boardingPass);

        log.info("Pase de abordaje usado para vuelo {}", flightCode);
    }
}
