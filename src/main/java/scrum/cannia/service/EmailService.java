package scrum.cannia.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    public void enviarPublicidad(String correo, String titulo, String mensajeHtml) throws IOException {

        Email from = new Email("sena.danielboss@gmail.com");
        Email to = new Email(correo);

        Content content = new Content("text/html", mensajeHtml);

        Mail mail = new Mail(from, titulo, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        sg.api(request);
    }

    // ENVÍO MASIVO
    public void enviarMasivo(List<String> correos, String titulo, String mensajeHtml) {
        correos.forEach(correo -> {
            try {
                enviarPublicidad(correo, titulo, mensajeHtml);
            } catch (Exception e) {
                System.out.println("Error enviando a: " + correo);
            }
        });
    }

}

