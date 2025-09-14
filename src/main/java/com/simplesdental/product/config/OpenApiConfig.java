package com.simplesdental.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server developmentServer = new Server()
                .url("http://localhost:8080")
                .description("Development Server");

        Contact contact = new Contact()
                .name("Simples Dental API Team")
                .email("api@simplesdental.com")
                .url("https://simplesdental.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Simples Dental Products API")
                .version("2.0.0")
                .description("API para gerenciamento de produtos da Simples Dental. " +
                           "Esta API oferece duas versões:\n\n" +
                           "- **V1** (`/api/products`): Formato clássico com códigos PROD-XXX\n" +
                           "- **V2** (`/api/v2/products`): Formato moderno com códigos inteiros\n\n" +
                           "Ambas as versões compartilham o mesmo banco de dados e funcionalidades.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(developmentServer));
    }
}