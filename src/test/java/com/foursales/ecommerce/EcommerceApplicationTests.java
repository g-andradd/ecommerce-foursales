package com.foursales.ecommerce;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EcommerceApplicationTests {

    @Test
    void deveInstanciarClassePrincipal() {
        assertThat(new EcommerceApplication()).isNotNull();
    }

    }
