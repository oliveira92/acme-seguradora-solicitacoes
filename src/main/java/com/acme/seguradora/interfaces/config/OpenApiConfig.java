package com.acme.seguradora.interfaces.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ACME Seguradora - API de Solicitações de Apólice")
                        .version("1.0.0")
                        .description("Microsserviço para gerenciamento de solicitações de apólice de seguros com arquitetura orientada a eventos")
                        .contact(new Contact()
                                .name("ACME Seguradora")
                                .email("contato@acme-seguradora.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
