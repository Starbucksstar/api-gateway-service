package com.scene.apigateway.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NacosDynamicRouteConfiguration {
    @Value("${spring.cloud.nacos.config.server-addr}")
    private String nacosConfigServer;
    @Value("${nacos.gateway.dynamic.route.dataId}")
    private String dynamicRouteDataId;
    @Value("${nacos.gateway.dynamic.route.groupId}")
    private String dynamicRouteGroupId;
}
