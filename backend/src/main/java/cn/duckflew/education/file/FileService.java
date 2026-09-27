package cn.duckflew.education.file;

import cn.duckflew.education.common.exception.BusinessException;
import cn.duckflew.education.common.exception.ErrorCode;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileService {

    private final FileObjectRepository repository;
    private final StorageService storageService;
    private final StorageProperties properties;

    public FileService(FileObjectRepository repository, StorageService storageService,
                       StorageProperties properties) {
        this.repository = repository;
        this.storageService = storageService;
        this.properties = properties;
    }

    @Transactional
    public FileInfo store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "上传文件不能为空");
        }
        String storageKey = buildStorageKey(file.getOriginalFilename());
        try {
            storageService.store(storageKey, file.getInputStream(), file.getSize());
        } catch (IOException e) {
            throw new UncheckedIOException("读取上传文件失败", e);
        }
        FileObject entity = new FileObject();
        entity.setStorageKey(storageKey);
        entity.setOriginalName(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        entity.setContentType(file.getContentType());
        entity.setSizeBytes(file.getSize());
        repository.save(entity);
        return FileInfo.from(entity, properties.publicBaseUrl());
    }

    @Transactional(readOnly = true)
    public FileObject getRequired(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Resource load(Long id) {
        return storageService.load(getRequired(id).getStorageKey());
    }

    private String buildStorageKey(String originalName) {
        String extension = "";
        if (originalName != null) {
            int dot = originalName.lastIndexOf('.');
            if (dot >= 0) {
                extension = originalName.substring(dot).toLowerCase(Locale.ROOT);
            }
        }
        LocalDate today = LocalDate.now();
        return "%d/%02d/%s%s".formatted(today.getYear(), today.getMonthValue(),
                UUID.randomUUID().toString().replace("-", ""), extension);
    }
}
