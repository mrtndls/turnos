package com.unla.grupo16.configurations.mail;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    // configura el sv SMTP de gmail
    // para enviar mails desde la aplicacion
    // como y donde se conect
    //@Value("${MAIL_USERNAME}")
    private String username = "martinabeldialessio@gmail.com";

    //@Value("${MAIL_PASSWORD}")
    private String password = "bmqjzqcifpjvrtsw";

    @Bean
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // puerto y host para gmail
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        // usa la pw de la cuenta de gmail
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        // habilitar auth SMTP
        props.put("mail.smtp.auth", "true");
        // habilitar STARTTLS
        props.put("mail.smtp.starttls.enable", "true");
        // habilitar trust para el sv gmaik
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        // habilitar starttls obligatorio
        props.put("mail.smtp.starttls.required", "true");

        return mailSender;
    }
}
