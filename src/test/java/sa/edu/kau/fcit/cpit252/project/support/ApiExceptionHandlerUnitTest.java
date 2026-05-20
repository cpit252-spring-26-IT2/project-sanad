package sa.edu.kau.fcit.cpit252.project.support;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerUnitTest {

    @Test
    void handlerBuildsPayloadForAllSupportedExceptions() throws Exception {
        ApiExceptionHandler handler = new ApiExceptionHandler();

        ResponseEntity<Map<String, Object>> badRequest = handler.handleIllegalArgument(
                new IllegalArgumentException("bad argument"));
        assertThat(badRequest.getStatusCode().value()).isEqualTo(400);
        assertThat(badRequest.getBody()).containsEntry("message", "bad argument");

        ResponseEntity<Map<String, Object>> unauthorized = handler.handleIllegalState(
                new IllegalStateException("not allowed"));
        assertThat(unauthorized.getStatusCode().value()).isEqualTo(401);
        assertThat(unauthorized.getBody()).containsEntry("message", "not allowed");

        ResponseEntity<Map<String, Object>> violation = handler.handleConstraintViolation(
                new ConstraintViolationException("constraint failed", null));
        assertThat(violation.getStatusCode().value()).isEqualTo(400);
        assertThat(violation.getBody()).containsEntry("message", "constraint failed");

        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(new Object(), "request");
        binding.addError(new FieldError("request", "rating", "Rating must be between 1 and 5"));
        Method method = ApiExceptionHandlerUnitTest.class.getDeclaredMethod("dummyMethod", String.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                new org.springframework.core.MethodParameter(method, 0),
                binding
        );

        ResponseEntity<Map<String, Object>> validation = handler.handleValidation(ex);
        assertThat(validation.getStatusCode().value()).isEqualTo(400);
        assertThat(validation.getBody()).containsEntry("message", "Rating must be between 1 and 5");
    }

    @SuppressWarnings("unused")
    private void dummyMethod(String value) {
    }
}
