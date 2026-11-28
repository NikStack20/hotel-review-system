package com.rating.api.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration 
public class OpenAPIConfig {
    
    @Bean 
    public OpenAPI CustomOpenAPI() {
        return new OpenAPI()
        .servers(List.of(
            new Server()
            .url("/")
            .description("API Gateway")
        ));
    }
}
