package com.unla.grupo16.services.interfaces;

import jakarta.mail.MessagingException;

public interface IEmailService {

    public void sendEmail(String to, String subject, String body) throws MessagingException;
}
