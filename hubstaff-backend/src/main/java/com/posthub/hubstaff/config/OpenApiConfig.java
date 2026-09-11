package com.posthub.hubstaff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI hubstaffOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hubstaff Workforce API")
                        .version("v1")
                        .description("API for employees, attendance, and leave management."));
    }
}
