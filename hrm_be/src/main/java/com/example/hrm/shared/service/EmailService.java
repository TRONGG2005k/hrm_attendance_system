package com.example.hrm.shared.service;

import com.example.hrm.modules.user.entity.UserAccount;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.baseUrl}")
    private String BASE_URL_FE;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendActivationEmail(UserAccount user, String activationToken) {
        String link = BASE_URL_FE + "?token=" + activationToken;
        String subject = "Kích hoạt tài khoản HRM";
        String body = "<p>Chào " + user.getEmployee().getLastName()
                + " " + user.getEmployee().getFirstName() + ",</p>"
                + "<p>Nhấn vào link bên dưới để kích hoạt tài khoản và đổi mật khẩu:</p>"
                + "<a href=\"" + link + "\">Kích hoạt tài khoản</a>"
                + "<p>Link có hiệu lực 15 phút.</p>";

        sendEmail(user.getEmployee().getEmail(), subject, body);
    }

    public void sendTestMail(String to) {
        String subject = "TEST MAIL HRM";
        String body = "<h3>Mail test thành công 🎉</h3>";
        sendEmail(to, subject, body);
    }


    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại", e);
        }
    }
}
