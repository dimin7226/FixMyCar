package com.fixmycar.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fix My Car API")
                        .version("1.0")
                        .description("API для управления автосервисом."));
    }
}
```# CODE

This commit is part of our strategy to resolve the Swagger API documentation generation issue, which requires addressing how custom Exception Handlers interact with Swagger's documentation generator. In our case, the `SwaggerExceptionHandler` class can override proper error reporting when Swagger attempts to generate documentation via the `/v3/api-docs` endpoint. By refining the `handleSwaggerException` method, we can ensure that Swagger-related exceptions are identified and properly channeled.

<mermaid>
graph TD;
    Start[Start: Refine Exception Handling for Swagger] --> A{Does current handler correctly identify Swagger exceptions?}
    A -- No --> B[Modify SwaggerExceptionHandler]
    B --> C[Add debugging logs for Swagger errors]
    C --> D[Pass non-Swagger errors to GlobalExceptionHandler]
    D --> E[Test if /v3/api-docs returns correct response]
</mermaid>

(modified src/main/java/com/fixmycar/exception/SwaggerExceptionHandler.java)```# DONE