package cn.duckflew.education.file;

import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * 文件存储抽象。默认本地磁盘实现，可替换为 S3/OSS 等。
 */
public interface StorageService {

    void store(String storageKey, InputStream content, long size);

    Resource load(String storageKey);

    void delete(String storageKey);
}
