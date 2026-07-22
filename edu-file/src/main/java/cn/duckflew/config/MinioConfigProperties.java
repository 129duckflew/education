package cn.duckflew.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "minio.config")
@Component
public class MinioConfigProperties
{
    private String endpoint;
    private String bucketName;
    private String accessKey;
    private String secretKey;
}
