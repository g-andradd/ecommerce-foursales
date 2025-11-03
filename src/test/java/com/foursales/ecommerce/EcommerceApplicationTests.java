package com.foursales.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EcommerceApplicationTests {

    @Test
    void deveInstanciarClassePrincipal() {
        assertThat(new EcommerceApplication()).isNotNull();
    }

    }
