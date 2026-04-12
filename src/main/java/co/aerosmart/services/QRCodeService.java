package co.aerosmart.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Servicio para generar códigos QR.
 * Los QR contienen tokens seguros, no datos personales.
 */
@Service
public class QRCodeService {

    /**
     * Genera un código QR en formato base64 a partir de un texto.
     * 
     * @param text El texto a codificar (típicamente un token seguro)
     * @param width Ancho del QR en píxeles
     * @param height Alto del QR en píxeles
     * @return String con la imagen QR en base64
     */
    public String generateQRCode(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrBytes = outputStream.toByteArray();

            return Base64.getEncoder().encodeToString(qrBytes);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generando código QR: " + e.getMessage(), e);
        }
    }

    /**
     * Genera un código QR con dimensiones por defecto (300x300).
     */
    public String generateQRCode(String text) {
        return generateQRCode(text, 300, 300);
    }
}
