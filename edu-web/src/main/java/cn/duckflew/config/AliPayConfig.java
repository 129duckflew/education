package cn.duckflew.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "alipay")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayConfig
{
    private String appId;
    private String alipayPublicKey;
    private String appPrivateKey;
    private String notifyUrl;
    private String returnUrl;
}
