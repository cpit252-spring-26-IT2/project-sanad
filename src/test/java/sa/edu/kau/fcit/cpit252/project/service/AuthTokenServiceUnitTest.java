package sa.edu.kau.fcit.cpit252.project.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthTokenServiceUnitTest {

    @Test
    void issueAndResolveTokenAndRejectInvalidInputs() {
        AuthTokenService service = new AuthTokenService(24);
        String token = service.issueToken(42L);

        assertThat(token).isNotBlank();
        assertThat(service.resolveUserId(token)).contains(42L);
        assertThat(service.resolveUserId(null)).isEmpty();
        assertThat(service.resolveUserId(" ")).isEmpty();
        assertThat(service.resolveUserId("missing")).isEmpty();
    }

    @Test
    void expiredTokenReturnsEmpty() throws InterruptedException {
        AuthTokenService service = new AuthTokenService(0);
        String token = service.issueToken(7L);
        Thread.sleep(5L);
        assertThat(service.resolveUserId(token)).isEmpty();
    }
}
