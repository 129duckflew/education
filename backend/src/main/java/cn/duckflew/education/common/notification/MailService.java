package cn.duckflew.education.common.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件发送。未配置 SMTP 时降级为日志输出，保证本地/演示可运行。
 */
@Slf4j
@Service
public class MailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final MailProperties properties;

    public MailService(ObjectProvider<JavaMailSender> mailSenderProvider, MailProperties properties) {
        this.mailSenderProvider = mailSenderProvider;
        this.properties = properties;
    }

    public void send(String to, String subject, String body) {
        JavaMailSender sender = properties.enabled() ? mailSenderProvider.getIfAvailable() : null;
        if (sender == null) {
            log.info("[MAIL-DEV] to={} subject={} body={}", to, subject, body);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.from());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        sender.send(message);
    }
}
