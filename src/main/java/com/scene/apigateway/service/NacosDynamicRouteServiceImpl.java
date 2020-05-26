package com.scene.apigateway.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class NacosDynamicRouteServiceImpl implements NacosDynamicRouteService {

    private final RouteDefinitionWriter routeDefinitionWriter;
    private final ApplicationEventPublisher publisher;

    @Override
    public String updateRoute(List<RouteDefinition> routeDefinitions) {
        for(RouteDefinition routeDefinition:routeDefinitions) {
            routeDefinitionWriter.delete(Mono.just(routeDefinition.getId()));
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
            publisher.publishEvent(new RefreshRoutesEvent(routeDefinition));
        }
        return "update route success";
    }
}
