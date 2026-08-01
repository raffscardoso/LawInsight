package com.raffs.LawInsight.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JwtPropertiesTest {

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void shouldLoadJwtPropertiesFromEnvironment() {
        assertThat(jwtProperties).isNotNull();
        assertThat(jwtProperties.getSecret()).isEqualTo("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        assertThat(jwtProperties.getExpirationMs()).isEqualTo(86400000L);
        assertThat(jwtProperties.getRefreshExpirationMs()).isEqualTo(604800000L);
    }
}
