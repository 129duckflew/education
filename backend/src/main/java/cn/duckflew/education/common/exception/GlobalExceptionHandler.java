package cn.duckflew.education.common.exception;

import cn.duckflew.education.common.api.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        ErrorCode code = ex.getErrorCode();
        return ResponseEntity.status(code.httpStatus())
                .body(ApiResponse.error(code.code(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.httpStatus())
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR.code(), message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraint(ConstraintViolationException ex) {
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.httpStatus())
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR.code(), ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("数据完整性冲突: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.httpStatus())
                .body(ApiResponse.error(ErrorCode.BAD_REQUEST.code(), "数据冲突，请检查输入"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(ErrorCode.FORBIDDEN.httpStatus())
                .body(ApiResponse.error(ErrorCode.FORBIDDEN.code(), ErrorCode.FORBIDDEN.defaultMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.httpStatus())
                .body(ApiResponse.error(ErrorCode.UNAUTHORIZED.code(), ErrorCode.UNAUTHORIZED.defaultMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        log.error("未处理异常", ex);
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.httpStatus())
                .body(ApiResponse.error(ErrorCode.INTERNAL_ERROR.code(), ErrorCode.INTERNAL_ERROR.defaultMessage()));
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
