package com.foursales.ecommerce.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        public static final String SECURITY_SCHEME_BEARER = "bearerAuth";

        @Bean
        public OpenAPI ecommerceOpenAPI() {
                return new OpenAPI()
                        .info(new Info()
                                .title("Ecommerce API")
                                .description("Plataforma de e-commerce da FourSales para gestão de produtos, pedidos e relatórios.")
                                .version("v1")
                                .contact(new Contact()
                                        .name("Equipe FourSales")
                                        .email("suporte@foursales.com"))
                                .license(new License()
                                        .name("Apache 2.0")
                                        .url("https://www.apache.org/licenses/LICENSE-2.0")))
                        .externalDocs(new ExternalDocumentation()
                                .description("Documentação do projeto")
                                .url("https://github.com/foursales/ecommerce"))
                        .addServersItem(new Server()
                                .url("http://localhost:8080")
                                .description("Ambiente local"))
                        .addServersItem(new Server()
                                .url("https://api.foursales.com")
                                .description("Ambiente de produção"))
                        .components(new Components()
                                .addSecuritySchemes(SECURITY_SCHEME_BEARER, new SecurityScheme()
                                        .name(SECURITY_SCHEME_BEARER)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_BEARER));
        }

        @Bean
        public GroupedOpenApi ecommerceGroupedOpenApi() {
                return GroupedOpenApi.builder()
                        .group("v1")
                        .displayName("Ecommerce API v1")
                        .pathsToMatch("/api/v1/**")
                        .packagesToScan("com.foursales.ecommerce.api")
                        .build();
        }
}
