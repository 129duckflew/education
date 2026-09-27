package cn.duckflew.education.common.notification;

import cn.duckflew.education.common.async.OutboxHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class SmsCodeOutboxHandler implements OutboxHandler {

    public static final String TYPE = "SMS_CODE";

    private final SmsService smsService;
    private final ObjectMapper objectMapper;

    public SmsCodeOutboxHandler(SmsService smsService, ObjectMapper objectMapper) {
        this.smsService = smsService;
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
        smsService.send(target, "【教授面对面】验证码 " + code + "，5 分钟内有效。");
    }
}
