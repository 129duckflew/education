package cn.duckflew.education.file;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileObjectRepository extends JpaRepository<FileObject, Long> {
    boolean existsByStorageKey(String storageKey);
}
