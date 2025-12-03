package com.portfolio.Utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendOtpEmail(String toEmail, String name, String email, String messageInfo) throws MessagingException {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("Feedback from Portfolio Website");
		message.setText("Hi Gautam, \n\nThe feedback received from Portfolio Website is here: \n\nEmail: " + email
				+ " \nName: " + name + "\n\nBelow is the message: \n" + messageInfo);

		mailSender.send(message);
	}
}
