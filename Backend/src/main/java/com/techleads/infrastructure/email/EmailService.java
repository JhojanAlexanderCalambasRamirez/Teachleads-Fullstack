package com.techleads.infrastructure.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarInventarioPdf(String destinatario, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("Reporte de Inventario - TeachLeads");
            helper.setText(
                    "<h2>Reporte de Inventario</h2>" +
                    "<p>Adjunto encontrará el reporte de inventario solicitado.</p>" +
                    "<p>Generado por el sistema TeachLeads.</p>",
                    true);

            helper.addAttachment("inventario.pdf",
                    () -> new java.io.ByteArrayInputStream(pdfBytes),
                    "application/pdf");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando email: " + e.getMessage(), e);
        }
    }
}
