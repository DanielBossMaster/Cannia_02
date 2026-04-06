package scrum.cannia.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${app.url}")
    private String appUrl;

    public void enviarCorreoRecuperacion(
            String email,
            String token){

        String link =
                appUrl + "/registro/reset-password?token=" + token;

        SimpleMailMessage mensaje =
                new SimpleMailMessage();

        mensaje.setTo(email);

        mensaje.setSubject(
                "Recuperar contraseña - Cannia");

        mensaje.setText(
                "Hola \n\n" +
                        "Haz clic en el enlace para cambiar tu contraseña:\n"
                        + link +
                        "\n\nEste enlace expira en 30 minutos."
        );

        if(mailSender != null){
            mailSender.send(mensaje);
        }else{
            System.out.println("JavaMailSender no disponible en este entorno");
        }
    }

    public void enviarPublicidad(String correo, String titulo, String mensajeHtml, byte[] imagenBytes, String nombreImagen) throws IOException {

        Email from = new Email("cannia.scrum2@gmail.com");
        Email to = new Email(correo);

        Content content = new Content("text/html", mensajeHtml);

        Mail mail = new Mail(from, titulo, to, content);

        // Si hay imagen, agregarla como inline
        if (imagenBytes != null) {
            com.sendgrid.helpers.mail.objects.Attachments adj =
                    new com.sendgrid.helpers.mail.objects.Attachments();

            adj.setContent(java.util.Base64.getEncoder().encodeToString(imagenBytes));
            adj.setType("image/png");
            adj.setFilename(nombreImagen);
            adj.setDisposition("inline");
            adj.setContentId("bannerImagen"); // CID

            mail.addAttachments(adj);
        }

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        sg.api(request);
    }


    // ENVÍO MASIVO
    public void enviarMasivo(List<String> correos, String titulo, String mensajeHtml, byte[] imagenBytes, String nombreImagen) {
        correos.forEach(correo -> {
            try {
                enviarPublicidad(correo, titulo, mensajeHtml, imagenBytes, nombreImagen);
            } catch (Exception e) {
                System.out.println("Error enviando a: " + correo);
            }
        });
    }

}

