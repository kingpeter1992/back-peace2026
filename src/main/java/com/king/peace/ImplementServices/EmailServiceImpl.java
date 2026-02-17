package com.king.peace.ImplementServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.king.peace.Dto.*;
import com.king.peace.Entitys.Client;
import com.king.peace.Entitys.Facture;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

@Service
public class EmailServiceImpl {

    @Autowired private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("kingproduct45@gmail.com"); // expéditeur
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            System.out.println("Email envoyé à : " + to);
        } catch (Exception e) {
            System.err.println("Erreur d'envoi d'email : " + e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email. Cause : " + e.getMessage(), e);
        }
    }



public void sendHtmlEmail(String to, String subject, String htmlContent) {
    try {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setFrom("kingproduct45@gmail.com");

        mailSender.send(mimeMessage);
        System.out.println("Email HTML envoyé à : " + to);
    } catch (MessagingException e) {
        throw new RuntimeException("Erreur d'envoi de mail HTML : " + e.getMessage(), e);
    }
}

    public void envoyerFacture(Client client, Facture facture, boolean refacturation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(client.getEmail());
        message.setSubject(refacturation ? 
            "Refacturation Contrat " + facture.getContrats().getRefContrats() :
            "Nouvelle Facture Contrat " + facture.getContrats().getRefContrats());

        String texte = String.format(
            "Bonjour %s,\n\n" +
            (refacturation ? "Une refacturation a été effectuée" : "Une facture a été générée") +
            " pour votre contrat %s.\n\n" +
            "Référence facture : %s\n" +
            "Nombre de gardiens : %d\n" +
            "Montant par gardien : %.2f %s\n" +
            "Nombre de jours : %d\n" +
            "Montant total : %.2f %s\n\n" +
            "Description : %s\n\n" +
            "Merci pour votre confiance.",
            client.getNom(),
            facture.getContrats().getRefContrats(),
            facture.getRefFacture(),
            facture.getNombreGardiens(),
            facture.getMontantParGardien(),
            facture.getDevise(),
            facture.getNombreJours(),
            facture.getMontantTotal(),
            facture.getDevise(),
            facture.getDescription()
        );

        message.setText(texte);
        mailSender.send(message);
    }
}






