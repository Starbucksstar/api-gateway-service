package com.scene.apigateway.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtTokenError {
    JWT_TOKEN_NOT_EXIST(401, "JwtToken is not exist"),
    JWT_TOKEN_EXPIRED(401, "JwtToken is expired"),
    JWT_TOKEN_ILLEGAL(403, "JwtToken is illegal"),
    SYSTEM_ERROR(500,"System error");

    private int errorCode;
    private String errorMsg;
}
