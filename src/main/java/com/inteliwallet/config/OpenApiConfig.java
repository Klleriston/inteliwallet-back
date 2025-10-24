package com.inteliwallet.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:3001}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        // Define o esquema de segurança JWT
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
            .info(new Info()
                .title("InteliWallet API")
                .description("API REST para InteliWallet - Sistema de Carteira Financeira Gamificada com features de gestão de transações, metas financeiras, amizades e sistema de pontuação.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("InteliWallet Team")
                    .email("contato@inteliwallet.com")
                    .url("https://github.com/inteliwallet"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort + "/api")
                    .description("Servidor de Desenvolvimento"),
                new Server()
                    .url("https://api.inteliwallet.com/api")
                    .description("Servidor de Produção")
            ))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Informe o token JWT obtido no login. Formato: Bearer {token}")));
    }
}