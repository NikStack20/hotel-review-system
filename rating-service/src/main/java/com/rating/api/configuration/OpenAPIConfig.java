package com.rating.api.configuration;

import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

public class OpenAPIConfig {
    
    public OpenAPI CustomOpenAPI() {
        return new OpenAPI()
        .servers(List.of(
            new Server()
            .url("/")
            .description("API Gateway")
        ));
    }
}
