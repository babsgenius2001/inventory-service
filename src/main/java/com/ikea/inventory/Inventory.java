package com.ikea.inventory;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Inventory Service", version = "1.0", description = "Inventory Service API"))
public class Inventory {

    public static void main(String[] args) {

        SpringApplication.run(Inventory.class, args);
    }

}