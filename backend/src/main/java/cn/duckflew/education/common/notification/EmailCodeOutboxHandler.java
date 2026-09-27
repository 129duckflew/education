package cn.duckflew.education.common.notification;

import cn.duckflew.education.common.async.OutboxHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class EmailCodeOutboxHandler implements OutboxHandler {
    public static final String TYPE = "EMAIL_CODE";

    private final MailService mailService;
    private final ObjectMapper objectMapper;

    public EmailCodeOutboxHandler(MailService mailService, ObjectMapper objectMapper) {
        this.mailService = mailService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public void handle(String payloadJson) throws Exception {
        JsonNode node = objectMapper.readTree(payloadJson);
        String target = node.path("target").asText();
        String code = node.path("code").asText();
        String purpose = node.path("purpose").asText();
        mailService.send(target, "教授面对面 - 验证码", "您的验证码是 " + code + "，用途：" + purpose + "，5 分钟内有效。");
    }
}
