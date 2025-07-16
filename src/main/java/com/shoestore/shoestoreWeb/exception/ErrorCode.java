package com.shoestore.shoestoreWeb.exception;

public enum ErrorCode {
    UNCATEGORIZED_ERROR(9999, "Uncategorized error"),
    INVALID_KEY(1000, "Uncategorized error"),
    USER_EXISTED(1001, "User existed"),
    USERNAME_INVALID(1002, "Username must be at least 3 character"),
    PASSWORD_INVALID(1003, "Password must be at least 8 character"),
    EMAIL_NOT_EXISTED(1004, "Email not existed"),
    UNAUTHORIZED(1005, "User is not authorized")
    ;
    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
