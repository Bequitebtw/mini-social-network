package com.bequitebtw.socialnetwork.notification;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotifier implements Notifier {
	private final JavaMailSender mailSender;
	@Value("${base.url}")
	private String baseUrl;

	public void sendVerificationToken(String to, String token) {
		String frontendLink = baseUrl + "/?screen=login&token=" + token;

		String htmlContent = """
				<html>
				    <body style="font-family: Arial, sans-serif; background-color: #f8f8f8; padding: 20px;">
				        <div style="background-color: #ffffff; padding: 20px; border-radius: 10px; text-align: center;">
				            <h2 style="color: #333333;">Добро пожаловать в SocialNetwork!</h2>
				            <p style="color: #555555;">Вы можете активировать ваш ваш Аккаунт, нажав на кнопку ниже:</p>
				            <a href="%s" style="display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; border-radius: 5px; text-decoration: none;">Подтвердить Email</a>
				            <p style="color: #999999; margin-top: 20px;">Если вы не регистрировались, просто проигнорируйте это письмо.</p>
				        </div>
				    </body>
				</html>
				""".formatted(frontendLink);

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(to);
			helper.setSubject("✅ Подтверждение аккаунта");
			helper.setFrom("your_email@gmail.com");
			helper.setText(htmlContent, true);

			mailSender.send(message);
			log.info("Message with account confirmation sent to {}", to);
		} catch (MessagingException e) {
			throw new IllegalStateException("Failed to send email", e);
		}
	}
}
