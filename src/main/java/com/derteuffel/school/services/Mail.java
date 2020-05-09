package com.derteuffel.school.services;

import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
public class Mail {
    final String APIKey = "d1d8822287e531e5ef55c30383fdc1f8";
    final String SecretKey = "4bac95879ebb788377886651c4597173";
    String From = "info@yesbanana.org";

    public String getFrom() {
        return From;
    }

    public Session getJavaMailSsender(){

    Properties props = new Properties ();

		props.put ("mail.smtp.host", "in.mailjet.com");
		props.put ("mail.smtp.socketFactory.port", "465");
		props.put ("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put ("mail.smtp.auth", "true");
		props.put ("mail.smtp.port", "465");

    Session session = Session.getDefaultInstance (props,
            new javax.mail.Authenticator ()
            {
                protected PasswordAuthentication getPasswordAuthentication ()
                {
                    return new PasswordAuthentication(APIKey, SecretKey);
                }
            });

    return session;
    }

    public void sender(String To, String subject, String text){
        try
        {

            Message message = new MimeMessage(getJavaMailSsender());
            message.setFrom (new InternetAddress(getFrom()));
            message.setRecipients (Message.RecipientType.TO, InternetAddress.parse(To));
            message.setSubject (subject);
            message.setText (text);

            Transport.send (message);
            System.out.println("i sent a mail");

        }
        catch (MessagingException e)
        {
            throw new RuntimeException (e);
        }
    }


}
