package com.siget.siget20.Controllers;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/Docente")
public class QuejasController {

    private final JavaMailSender mailSender;

    @Value("${siget.quejas.destino}")
    private String correoDestino;

    public QuejasController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/Quejas/enviar")
    public String enviarQueja(@RequestParam("nombre") String nombre,
                              @RequestParam("email") String email,
                              @RequestParam("asunto") String asunto,
                              @RequestParam("descripcion") String descripcion,
                              @RequestParam(value = "archivo", required = false) MultipartFile archivo,
                              @RequestParam("tipoSistema") String tipoSistema,
                              RedirectAttributes redirectAttributes) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            boolean tieneAdjunto = (archivo != null && !archivo.isEmpty());
            MimeMessageHelper helper = new MimeMessageHelper(message, tieneAdjunto, "UTF-8");

            helper.setTo(correoDestino);
            helper.setSubject("[QUEJA " + tipoSistema + "] " + asunto);

            String cuerpo = "Se ha recibido una nueva queja.\n\n"
                    + "Sistema: " + tipoSistema + "\n"
                    + "Nombre: " + nombre + "\n"
                    + "Correo del docente: " + email + "\n\n"
                    + "Descripción:\n" + descripcion;

            helper.setText(cuerpo, false);

            if (tieneAdjunto) {
                helper.addAttachment(archivo.getOriginalFilename(), archivo);
            }

            mailSender.send(message);

            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Tu queja ha sido enviada correctamente. ¡Gracias por tu comentario!");

        } catch (MessagingException  e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError",
                    "Ocurrió un error al enviar la queja. Inténtalo de nuevo más tarde.");
        }

        return "redirect:/Docente/Quejas";
    }
}
