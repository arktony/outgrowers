package com.farm_erp.utilities;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.farm_erp.settings.domains.Business;
import com.farm_erp.statics.Constants;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;

@ApplicationScoped
public class EmailServices {

	private static final Logger logger = LoggerFactory.getLogger(EmailServices.class.getName());

	@Inject
	Template usercreated, userreset;

	@Inject
	ReactiveMailer reactiveMailer;

	public Boolean createAndSendCredentialsEmail(String name, String email, String password, Business business) {

		String mail = usercreated.data("name", name, "email", email, "password", password, "serverLink",
				Constants.loginLink, "title", "Account Created").data("business", business.name).render();

		reactiveMailer.send(Mail.withHtml(email, "Account Created", mail)).subscribeAsCompletionStage().thenRun(() -> {
			System.out.println("Email sent Successfully ***");
		});

		return true;

	}

	public Boolean ReSendCredentialsEmail(String name, String email, String password, Business business) {

		String mail = userreset.data("name", name, "email", email, "password", password, "serverLink",
				Constants.loginLink, "title", "Account Reset").data("business", business.name).render();

		reactiveMailer.send(Mail.withHtml(email, "Account Reset", mail)).subscribeAsCompletionStage().thenRun(() -> {
			System.out.println("Email sent Successfully ***");
		});

		return true;

	}

}
