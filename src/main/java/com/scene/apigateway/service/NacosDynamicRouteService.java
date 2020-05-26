package com.scene.apigateway.service;

import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;

public interface NacosDynamicRouteService {
    String updateRoute(List<RouteDefinition> routeDefinitions);
}
