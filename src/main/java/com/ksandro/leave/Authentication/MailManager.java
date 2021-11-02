package com.ksandro.leave.Authentication;

import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class MailManager {
	
	public JavaMailSender getJavaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost("smtp.gmail.com");
	    mailSender.setPort(587);
	    
	    mailSender.setUsername("ffortnite40@gmail.com");
	    mailSender.setPassword("Fortnite01");
	    
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
	    
	    return mailSender;
	}
	
	public void sendSimpleMessage(String to, String subject, String text) {
		        SimpleMailMessage message = new SimpleMailMessage(); 
		        message.setFrom("noreply@company.com");
		        message.setTo(to); 
		        message.setSubject(subject); 
		        message.setText(text);
		        getJavaMailSender().send(message);
		    }
	
	
	public String getBodyResetPassword(String name, String lastName, String passwordGenerated){
		String temp = "";

		
			temp = "<h1>New Temporary Password</h1>" +
					"Dear " + name + " " + lastName + "," +
					"<br />" +
					"<br/> Password: <b>" + passwordGenerated + "</b> <br /> ";
		

		return temp;
	}
	
	public String getBodyChangeStatusApplication(String name, String lastName, String status,String reason){
		String temp = "";

		
			temp = "<h1>Application Answer</h1>" +
					"Dear " + name + " " + lastName + "," +
					"<br /> your application for leave has been " + status +" ."+
					"<br/> Reason: <b>" + reason + "</b> <br /> ";
		return temp;
	}
}
