package com.scene.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scene.apigateway.constant.JwtTokenError;
import com.scene.apigateway.output.TokenErrorOutputDTO;
import com.scene.apigateway.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenAuthFilter implements GlobalFilter, Ordered {
    private static final String BEARER = "Bearer ";
    private final RedisTemplate<String, String> redisTemplate;
    private static final String LOGIN_URL = "/api/v1/account/login";
    @Value("${jwt.token.prefix}")
    private String TOKEN_PREFIX = "default";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        ServerHttpResponse response = exchange.getResponse();
        try {
            if (!exchange.getRequest().getPath().toString().equals(LOGIN_URL)) {
                if (StringUtils.isBlank(token)) {
                    return buildDataBufferAndWriteResponse(response, new TokenErrorOutputDTO(JwtTokenError.JWT_TOKEN_NOT_EXIST));
                } else {
                    final Long userId = JwtUtils.validateJwtToken(token);
                    String jwtToken = StringUtils.substringAfter(token, BEARER);
                    String key = TOKEN_PREFIX + userId;
                    if (!redisTemplate.hasKey(key)) {
                        throw new ExpiredJwtException(JwtUtils.getJwtTokenHeader(jwtToken), JwtUtils.getJwtTokenClaims(jwtToken), "token is not exist in redis");
                    }
                    if (!redisTemplate.opsForValue().get(key).equals(jwtToken)) {
                        throw new JwtException("JwtToken is illegal");
                    }
                }
            }
        } catch (ExpiredJwtException e) {
            log.error("[JwtToken expired], error info={}", e.getMessage(), e);
            return buildDataBufferAndWriteResponse(response, new TokenErrorOutputDTO(JwtTokenError.JWT_TOKEN_EXPIRED));
        } catch (JwtException e) {
            log.error("[JwtToken illegal], error info={}", e.getMessage(), e);
            return buildDataBufferAndWriteResponse(response, new TokenErrorOutputDTO(JwtTokenError.JWT_TOKEN_ILLEGAL));
        } catch (Exception e) {
            log.error("JwtToken error, error info={}", e.getMessage(), e);
            return buildDataBufferAndWriteResponse(response, new TokenErrorOutputDTO(JwtTokenError.SYSTEM_ERROR));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Void> buildDataBufferAndWriteResponse(ServerHttpResponse response, TokenErrorOutputDTO tokenErrorOutputDTO) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] data = objectMapper.writeValueAsBytes(tokenErrorOutputDTO);
            DataBuffer buffer = response.bufferFactory().wrap(data);
            response.setStatusCode(HttpStatus.valueOf(tokenErrorOutputDTO.getErrorCode()));
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Json process occur error ,info={}", e.getMessage(), e);
        }
        return null;
    }
}
