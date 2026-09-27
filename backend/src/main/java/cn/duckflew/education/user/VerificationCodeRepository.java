package cn.duckflew.education.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findFirstByTargetAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(
            String target, String purpose);
}
