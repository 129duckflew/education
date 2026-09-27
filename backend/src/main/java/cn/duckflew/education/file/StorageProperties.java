package cn.duckflew.education.file;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage.local")
public record StorageProperties(String root, String publicBaseUrl) {
}
