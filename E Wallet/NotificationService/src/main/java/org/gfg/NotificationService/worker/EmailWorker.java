package org.gfg.NotificationService.worker;

import jakarta.mail.internet.MimeMessage;
import org.gfg.NotificationService.util.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailWorker {


    @Autowired
    JavaMailSender javaMailSender;

    public void sendNotification(String email, String name, String documentNo, String documentType){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            System.out.println("inside the send email notification method");
            MimeMessageHelper mail = new MimeMessageHelper(mimeMessage, true);
            String emailContent = EmailTemplate.EMAIL_TEMPLATE;
            emailContent = emailContent.replaceAll("==name==",name);
            emailContent = emailContent.replaceAll("==document==",documentType);
            emailContent = emailContent.replaceAll("==documentNo==",documentNo);
            mail.setText(emailContent,true);
            mail.setTo(email);
            mail.setFrom("gfgwallet64@gmail.com");
            mail.setSubject("Welcome to JBDL E Wallet Application");
            javaMailSender.send(mimeMessage);
            System.out.println("Email has been sent to User");
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
