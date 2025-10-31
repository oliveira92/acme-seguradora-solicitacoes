package com.acme.seguradora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.acme.seguradora")
@EntityScan(basePackages = "com.acme.seguradora.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "com.acme.seguradora.infrastructure.persistence.repository")
public class AcmeSeguradoraSolicitacoesDeApoliceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcmeSeguradoraSolicitacoesDeApoliceApplication.class, args);
	}

}
