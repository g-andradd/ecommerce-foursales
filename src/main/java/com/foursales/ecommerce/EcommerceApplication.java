package com.foursales.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.foursales.ecommerce.infra.security.PropriedadesJwt;

@SpringBootApplication
@EnableConfigurationProperties(PropriedadesJwt.class)
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

}
