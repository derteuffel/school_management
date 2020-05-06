package com.derteuffel.school;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class SchoolApplication extends SpringBootServletInitializer{

	public static void main(String[] args) throws MailjetException, MailjetSocketTimeoutException {
		SpringApplication.run(SchoolApplication.class, args);

		MailjetClient client;
		MailjetRequest request;
		MailjetResponse response;
		client = new MailjetClient(System.getenv("d1d8822287e531e5ef55c30383fdc1f8"), System.getenv("4bac95879ebb788377886651c4597173"), new ClientOptions("v3.1"));
		request = new MailjetRequest(Emailv31.resource)
				.property(Emailv31.MESSAGES, new JSONArray()
						.put(new JSONObject()
								.put(Emailv31.Message.FROM, new JSONObject()
										.put("Email", "info@yesbanana.org")
										.put("Name", "Yesbanana team"))
								.put(Emailv31.Message.TO, new JSONArray()
										.put(new JSONObject()
												.put("Email", "derteuffel0@gmail.com")
												.put("Name", "Stephane derteuffel")))
								.put(Emailv31.Message.SUBJECT, "Your email flight plan!")
								.put(Emailv31.Message.TEXTPART, "Dear passenger 1, welcome to Mailjet! May the delivery force be with you!")
								.put(Emailv31.Message.HTMLPART, "<h3>Dear passenger 1, welcome to <a href=\"https://www.mailjet.com/\">Mailjet</a>!</h3><br />May the delivery force be with you!")));
		response = client.post(request);
		System.out.println(response.getStatus());
		System.out.println(response.getData());
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SchoolApplication.class);
	}



}
