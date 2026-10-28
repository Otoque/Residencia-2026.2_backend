package com.bancodobrasil.residencia.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginAttemptServiceTest {

    @Test
    void bloqueiaDepoisDoLimiteDeFalhas() {
        LoginAttemptService service = new LoginAttemptService(3, 15);

        service.recordFailure("ip|admin");
        service.recordFailure("ip|admin");
        assertThat(service.isBlocked("ip|admin")).isFalse();

        service.recordFailure("ip|admin");
        assertThat(service.isBlocked("ip|admin")).isTrue();
        assertThat(service.isBlocked("outro-ip|admin")).isFalse();
    }

    @Test
    void loginComSucessoZeraTentativas() {
        LoginAttemptService service = new LoginAttemptService(2, 15);

        service.recordFailure("k");
        service.reset("k");
        service.recordFailure("k");

        assertThat(service.isBlocked("k")).isFalse();
    }
}
