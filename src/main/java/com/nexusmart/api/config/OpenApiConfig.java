package com.nexusmart.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
// This annotation defines the security scheme we'll use
@SecurityScheme(
        name = "bearerAuth", // A name to reference this scheme
        type = SecuritySchemeType.HTTP, // The type of security
        bearerFormat = "JWT", // The format of the token
        scheme = "bearer" // The scheme to be used (bearer)
)
// This annotation provides general API info and applies the security globally
@OpenAPIDefinition(
        info = @Info(title = "NexusMart API", version = "v1", description = "API for the NexusMart E-commerce Platform"),
        // This applies the "bearerAuth" security scheme to all endpoints
        security = @SecurityRequirement(name = "bearerAuth")
)
public class OpenApiConfig {
}
