package api.ytter.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendVerificationCode(String to, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("verification for ytter.lv");
        String text = "Hello! To verify your account on ytter.lv you have to follow this link: " + verificationLink;
        message.setText(text);
        mailSender.send(message);
    }

}
