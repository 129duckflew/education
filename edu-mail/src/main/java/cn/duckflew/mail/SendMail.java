package cn.duckflew.mail;


import cn.duckflew.enums.RabbitMailType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Map;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
@Component
@Slf4j
public class SendMail
{
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    MailProperties mailProperties;
    @RabbitListener(queues = {"mail.email_check"})
    public void send(Map<String,Object> checkMailMap)
    {
        log.info("监听到发送邮件的任务");
        String email = checkMailMap.get("email").toString();
        String checkCode = checkMailMap.get("checkCode").toString();
        Integer mailTypeCode = (Integer) checkMailMap.get("mailTypeCode");
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper= new MimeMessageHelper(message);
        try
        {
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(email);
            if (mailTypeCode.equals(RabbitMailType.REGISTER_MAIL.getCode()))
            helper.setSubject("注册验证邮件");
            else if (mailTypeCode.equals(RabbitMailType.BIND_MAIL.getCode()))
            helper.setSubject("绑定邮箱验证邮件");
            else if (mailTypeCode.equals(RabbitMailType.CHANGE_PASSWORD_MAIL.getCode()))
                helper.setSubject("修改密码验证邮件");
            else if (mailTypeCode.equals(RabbitMailType.LOGIN_CHECK_MAIL.getCode()))
                helper.setSubject("登录验证邮件");
            else helper.setSubject("普通邮件");
            helper.setSentDate(new Date());
            Context context=new Context();
            context.setVariable("checkCode",checkCode);
            String mail = templateEngine.process("mail", context);
            helper.setText(mail,true);
            javaMailSender.send(message);
        } catch (MessagingException e)
        {
            e.printStackTrace();
        }
    }

}