package com.scene.apigateway.listener;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.scene.apigateway.config.NacosDynamicRouteConfiguration;
import com.scene.apigateway.service.NacosDynamicRouteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class NacosDynamicRouteListener {

    private final NacosDynamicRouteService nacosDynamicRouteService;
    private final NacosDynamicRouteConfiguration nacosDynamicRouteConfiguration;

    public NacosDynamicRouteListener(NacosDynamicRouteService nacosDynamicRouteService,
                                     NacosDynamicRouteConfiguration nacosDynamicRouteConfiguration) {
        this.nacosDynamicRouteConfiguration = nacosDynamicRouteConfiguration;
        this.nacosDynamicRouteService = nacosDynamicRouteService;
        addListener();
    }

    /**
     * Spring Cloud Nacos Do Not Support @NacosConfigListener
     * Use NacosFactory
     */
    private void addListener() {
        try {
            ConfigService configService = NacosFactory.createConfigService(nacosDynamicRouteConfiguration.getNacosConfigServer());
            initDynamicRouteFromNacos(configService);
            configService.addListener(nacosDynamicRouteConfiguration.getDynamicRouteDataId(), nacosDynamicRouteConfiguration.getDynamicRouteGroupId(), new AbstractListener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("Received Nacos dynamic route change event, info={}", configInfo);
                    nacosDynamicRouteService.updateRoute(parseDynamicRouteJson(configInfo));
                }
            });
        } catch (NacosException e) {
            log.error("Nacos add listener error,info={}", e.getMessage(), e);
        }
    }

    private void initDynamicRouteFromNacos(ConfigService configService) throws NacosException {
        final String config = configService.getConfig(nacosDynamicRouteConfiguration.getDynamicRouteDataId(), nacosDynamicRouteConfiguration.getDynamicRouteGroupId(), 500);
        log.info("Init nacos dynamic route when gateway started, route info={}", config);
        nacosDynamicRouteService.updateRoute(parseDynamicRouteJson(config));
    }

    private List<RouteDefinition> parseDynamicRouteJson(String content) {
        if (StringUtils.isNotEmpty(content)) {
            return JSONObject.parseArray(content, RouteDefinition.class);
        }
        return new ArrayList<>(0);
    }

}
