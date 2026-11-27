package com.fatec.controleveicular;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "API de Controle Veicular",
        description = "API RESTful para controle do seu carro: cadastro de veículos, "
                + "acompanhamento de manutenções e gestão da documentação (CRLV, IPVA, seguro etc.).",
        version = "1.0.0",
        contact = @Contact(name = "Projeto de Programação Orientada a Objetos - Fatec Itapetininga")
    )
)
public class ControleVeicularApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControleVeicularApplication.class, args);
    }
}
