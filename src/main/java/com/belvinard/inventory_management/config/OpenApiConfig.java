package com.belvinard.inventory_management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Value("${server.port}")
    private String serverPort;
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Management API")
                        .description("REST API for managing inventory items, stock levels, and warehouse operations")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("basicAuth", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Basic Authentication (username: admin, password: password)")));
    }
    
    @Bean
    public OpenApiCustomiser openApiCustomiser() {
        return openApi -> {
            Server server = new Server();
            server.setUrl("http://localhost:" + serverPort);
            server.setDescription("Development Server");
            openApi.addServersItem(server);
        };
    }
}
