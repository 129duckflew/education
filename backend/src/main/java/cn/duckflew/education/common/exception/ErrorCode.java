package cn.duckflew.education.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 业务错误码。code 为业务码，httpStatus 决定 HTTP 状态。
 */
public enum ErrorCode {

    BAD_REQUEST(40000, HttpStatus.BAD_REQUEST, "请求参数错误"),
    VALIDATION_ERROR(40001, HttpStatus.BAD_REQUEST, "参数校验失败"),

    UNAUTHORIZED(40100, HttpStatus.UNAUTHORIZED, "未登录或登录已过期"),
    INVALID_CREDENTIALS(40101, HttpStatus.UNAUTHORIZED, "用户名或密码错误"),
    CODE_INVALID(40002, HttpStatus.BAD_REQUEST, "验证码无效"),
    CODE_EXPIRED(40003, HttpStatus.BAD_REQUEST, "验证码已过期"),

    FORBIDDEN(40300, HttpStatus.FORBIDDEN, "无权访问"),
    ACCOUNT_DISABLED(40301, HttpStatus.FORBIDDEN, "账号已被禁用"),

    NOT_FOUND(40400, HttpStatus.NOT_FOUND, "资源不存在"),

    USER_ALREADY_EXISTS(40901, HttpStatus.CONFLICT, "用户已存在"),
    EMAIL_ALREADY_EXISTS(40902, HttpStatus.CONFLICT, "邮箱已被占用"),
    PHONE_ALREADY_EXISTS(40903, HttpStatus.CONFLICT, "手机号已被占用"),
    USERNAME_ALREADY_EXISTS(40904, HttpStatus.CONFLICT, "用户名已被占用"),
    NAME_ALREADY_EXISTS(40905, HttpStatus.CONFLICT, "名称已存在"),

    NOT_PROFESSOR(42201, HttpStatus.UNPROCESSABLE_ENTITY, "该用户不是教授"),
    NOT_APPROVED_PROFESSOR(42202, HttpStatus.UNPROCESSABLE_ENTITY, "教授资格未通过审核"),
    QUESTION_NOT_FOUND(42203, HttpStatus.UNPROCESSABLE_ENTITY, "问题不存在"),
    ANSWER_NOT_FOUND(42204, HttpStatus.UNPROCESSABLE_ENTITY, "回答不存在"),
    ORDER_NOT_FOUND(42205, HttpStatus.UNPROCESSABLE_ENTITY, "订单不存在"),
    FILE_NOT_FOUND(42206, HttpStatus.UNPROCESSABLE_ENTITY, "文件不存在"),
    ANSWER_ALREADY_EXISTS(42207, HttpStatus.UNPROCESSABLE_ENTITY, "该问题已作答"),
    USER_NOT_FOUND(42208, HttpStatus.UNPROCESSABLE_ENTITY, "用户不存在"),
    PROFESSOR_PROFILE_NOT_FOUND(42209, HttpStatus.UNPROCESSABLE_ENTITY, "教授资料不存在"),

    INTERNAL_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");

    private final int code;
    private final HttpStatus httpStatus;
    private final String defaultMessage;

    ErrorCode(int code, HttpStatus httpStatus, String defaultMessage) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    public int code() {
        return code;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}
