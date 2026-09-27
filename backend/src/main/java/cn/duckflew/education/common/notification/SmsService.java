package cn.duckflew.education.common.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 短信发送。默认仅记录日志，后续可替换为阿里云等实现。
 */
@Slf4j
@Service
public class SmsService {

    public void send(String phone, String text) {
        log.info("[SMS-DEV] to={} text={}", phone, text);
    }
}
