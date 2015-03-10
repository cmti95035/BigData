package com.cmti.analytics.util;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;


public class EmailUtil {

    public static void sendEmail(String subject, String msg, String attachmentPath, String... toAddress) throws EmailException {//TODO toAddress array
    	Email email;
    	if(attachmentPath==null){
    		email = new SimpleEmail();
    	}else{
    		email = new MultiPartEmail();

    		  EmailAttachment attachment = new EmailAttachment();
    		  attachment.setPath(attachmentPath);
    		  attachment.setDisposition(EmailAttachment.ATTACHMENT);
//    		  attachment.setDescription("description");
    		  //attachment.setName("data export");
    		  ((MultiPartEmail)email).attach(attachment);
    	}
    	email.setHostName("smtp.googlemail.com");//FIXME use bn's mail server
    	//email.setSmtpPort(465);
    	email.setAuthenticator(new DefaultAuthenticator("cxhgdd@gmail.com", "Je55ica168"));//FIXME
    	email.setSSLOnConnect(true);
    	email.setFrom("analytics_report@book.com");
    	email.setSubject(subject);
    	email.setMsg(msg);
    	email.addTo(toAddress);
    	email.send();
	}

	
    public static void main(String[] args) throws Exception{
    	sendEmail("TestMail", "This is a test mail ... :-)", null, "gmo@book.com");
    }
}