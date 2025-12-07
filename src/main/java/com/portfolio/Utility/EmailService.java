package com.portfolio.Utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;

@Service
public class EmailService {

	private final MailjetClient client;

	public EmailService(@Value("${MJ_APIKEY_PUBLIC}") String apiKey, @Value("${MJ_APIKEY_PRIVATE}") String apiSecret,
			@Value("${EMAIL_USER}") String senderEmail) {
		this.senderEmail = senderEmail;
		ClientOptions options = ClientOptions.builder().apiKey(apiKey).apiSecretKey(apiSecret).build();
		this.client = new MailjetClient(options);
	}

	private final String senderEmail;

	public void sendFeedbackEmail(String name, String email, String messageInfo) {
		JSONObject message = new JSONObject()
				.put("From", new JSONObject().put("Email", senderEmail).put("Name", "Gautam Singhal"))
				.put("To",
						new JSONArray().put(new JSONObject().put("Email", senderEmail).put("Name", "Gautam Singhal")))
				.put("Subject", "Feedback from Wrap & Wow Website")
				.put("TextPart", "Hi Gautam, \n\nThe feedback received from Wrap & Wow Website is here: \n\nEmail: "
						+ email + " \nName: " + name + "\n\nBelow is the message: \n" + messageInfo);

		MailjetRequest request = new MailjetRequest(Emailv31.resource).property(Emailv31.MESSAGES,
				new JSONArray().put(message));

		try {
			MailjetResponse response = client.post(request);
			System.out.println("Mailjet Status: " + response.getStatus());
			System.out.println(response.getData());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to send OTP email via Mailjet");
		}
	}
}
