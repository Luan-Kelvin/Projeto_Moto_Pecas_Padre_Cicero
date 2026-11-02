package com.motopecas.padrecicero.motopecas.padrecicero.security;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.email.ErrorSendingEmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailVerificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String ORIGINATING_EMAIL;

    private static final String SENDER_NAME = "Moto Peças Padre Cícero";

    private static final String URL_SITE = "http://localhost:8080";

    @Async
    public void sendMail(String recipient, String subject, String content) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setFrom(ORIGINATING_EMAIL, SENDER_NAME);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new ErrorSendingEmailException("ERRO! Não foi possível realizar envio de email.", e);
        }

        javaMailSender.send(message);
    }

    @Async
    public void sendVerificationEmail(Employee employee) throws MessagingException {
        String subject = "AQui está seu email para verificação.";
        String content  = String.format("""
                    Olá, %s, <br>
                    Por favor, clique no link abaixo para verificar sua conta: <br>
                    <h3><a href=\"%s\" target=\"_self\">VERIFICAR</a></h3>
                    Moto Peças Padre Cícero :)
                """, employee.getName(), URL_SITE + "/employee/verify-account?token=" + employee.getVerificationToken());

        sendMail(employee.getEmail(), subject, content);
    }
}
