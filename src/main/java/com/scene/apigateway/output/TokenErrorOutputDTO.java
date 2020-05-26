package com.scene.apigateway.output;

import com.scene.apigateway.constant.JwtTokenError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenErrorOutputDTO {
    private int errorCode;
    private String errorMsg;

    public TokenErrorOutputDTO(JwtTokenError tokenError) {
        this.errorCode = tokenError.getErrorCode();
        this.errorMsg = tokenError.getErrorMsg();
    }
}
