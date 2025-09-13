package com.belvinard.inventory_management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Value("${server.port}")
    private String serverPort;
    
    @Bean
    public OpenAPI openAPI() {
        String description = "REST API for managing inventory items, stock levels, and warehouse operations\n\n" +
                "**Demo Test Users:**\n" +
                "- **Admin**: username: `admin`, password: `password`\n" +
                "- **Manager**: username: `manager`, password: `password`\n" +
                "- **Sales**: username: `sales`, password: `password`\n" +
                "- **User**: username: `user`, password: `password`\n\n" +
                "Use these credentials with Basic Authentication to test the API endpoints.";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Management API")
                        .description(description)
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("BearerAuthentication - Use demo credentials from API description")));
    }
    
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            Server server = new Server();
            server.setUrl("http://localhost:" + serverPort);
            server.setDescription("Development Server");
            openApi.addServersItem(server);
        };
    }
}
