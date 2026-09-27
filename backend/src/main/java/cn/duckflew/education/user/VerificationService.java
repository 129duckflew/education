package cn.duckflew.education.user;

import cn.duckflew.education.common.async.OutboxService;
import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import cn.duckflew.education.common.notification.EmailCodeOutboxHandler;
import cn.duckflew.education.common.notification.SmsCodeOutboxHandler;
import cn.duckflew.education.common.notification.VerificationPayload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@Service
public class VerificationService {

    private static final Duration TTL = Duration.ofMinutes(5);

    private final VerificationCodeRepository repository;
    private final OutboxService outboxService;
    private final SecureRandom random = new SecureRandom();

    public VerificationService(VerificationCodeRepository repository, OutboxService outboxService) {
        this.repository = repository;
        this.outboxService = outboxService;
    }

    @Transactional
    public void sendCode(String target, VerificationCode.Channel channel, VerificationPurpose purpose) {
        String code = String.format("%06d", random.nextInt(1_000_000));
        VerificationCode entity = new VerificationCode();
        entity.setTarget(target);
        entity.setChannel(channel);
        entity.setPurpose(purpose.name());
        entity.setCode(code);
        entity.setExpiresAt(Instant.now().plus(TTL));
        repository.save(entity);

        String type = channel == VerificationCode.Channel.EMAIL
                ? EmailCodeOutboxHandler.TYPE
                : SmsCodeOutboxHandler.TYPE;
        outboxService.enqueue(type, new VerificationPayload(target, code, purpose.name()));
    }

    /**
     * 校验并消费验证码，失败抛出业务异常。
     */
    @Transactional
    public void verify(String target, VerificationPurpose purpose, String code) {
        if (code == null || code.isBlank()) {
            throw new BusinessException(ErrorCode.CODE_INVALID, "验证码不能为空");
        }
        VerificationCode entity = repository
                .findFirstByTargetAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(target, purpose.name())
                .orElseThrow(() -> new BusinessException(ErrorCode.CODE_INVALID, "请先获取验证码"));
        if (entity.isExpired()) {
            throw new BusinessException(ErrorCode.CODE_EXPIRED);
        }
        if (!entity.getCode().equals(code)) {
            throw new BusinessException(ErrorCode.CODE_INVALID);
        }
        entity.setConsumedAt(Instant.now());
        repository.save(entity);
    }
}
