package com.farm_erp.utilities;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@ApplicationScoped
public class Lazy {

	public static String URLEncode(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getCause());
		}
	}

	public static String URLDecode(String value) {
		try {
			return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getCause());
		}
	}

	public static Boolean sendEmail(String addresses, String subject, String messageContent) {
		try {
			ExecutorService threadExecutor1 = Executors.newCachedThreadPool();
			Thread_EmailSender task1 = new Thread_EmailSender(addresses, subject, messageContent);
			threadExecutor1.execute(task1);
			threadExecutor1.shutdown();
			threadExecutor1.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

			return true;

		} catch (Exception e) {

			return false;
		}

	}

	public static class Thread_EmailSender extends Thread {

		String addresses;
		String subject;
		String messageContent;

		// boolean to set false when message is not sent and True when the message has
		// been sent
		boolean sent = false;

		public Thread_EmailSender(String addresses, String subject, String messageContent) {
			this.addresses = addresses;
			this.subject = subject;
			this.messageContent = messageContent;
		}

		@Override
		public void run() {

			// the senders ID: all emails will appear to have been sent by this email
			final String username = "Ark@arkprogramers.com";

			// the password to the email server of the senders ID
			final String password = "the4rk";

			// Authentication properties
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "mail.arkprogramers.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			try {

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress("Ark@arkprogramers.com"));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(addresses));
				message.setSubject(subject);
				message.setContent(messageContent, "text/html");

				Transport.send(message);

				sent = true;
				System.out.println("Email sent successfully!");

			} catch (MessagingException e) {
				sent = false;
				throw new RuntimeException(e);

			}

		}
	}
}
