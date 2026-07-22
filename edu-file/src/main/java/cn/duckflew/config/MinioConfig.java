package cn.duckflew.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfig
{
    @Autowired
    private MinioConfigProperties properties;

    @Bean
    public MinioClient minioClient()
    {
        log.info("minioClient初始化,properties={}",properties.toString());
        return MinioClient.builder()
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .endpoint(properties.getEndpoint())
                .build();
    }
}
