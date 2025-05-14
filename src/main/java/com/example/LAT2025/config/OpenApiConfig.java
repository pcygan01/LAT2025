package com.example.LAT2025.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI charityApiDoc() {
        return new OpenAPI()
                .info(new Info()
                        .title("Charity Collection Box Management API")
                        .description("API for managing collection boxes and fundraising events")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Charity Organization")
                                .email("contact@charity.org"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ));
    }
} 