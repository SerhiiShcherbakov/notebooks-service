package com.serhiishcherbakov.notebooks.security;

import com.serhiishcherbakov.notebooks.config.SecurityProperties;
import com.serhiishcherbakov.notebooks.exception.AppException;
import com.serhiishcherbakov.notebooks.exception.Error;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {ApiKeyInterceptor.class})
@EnableConfigurationProperties(SecurityProperties.class)
class ApiKeyInterceptorTest {
    private static final String API_KEY_HEADER = "X-Api-Key";

    @Autowired
    private ApiKeyInterceptor apiKeyInterceptor;

    @Test
    void should_returnTrue_when_apiKeyIsValid()  {
        var request = new MockHttpServletRequest();
        request.addHeader(API_KEY_HEADER, "0d5b3d09-07a2-467a-9b3d-0907a2a67a83");
        var response = new MockHttpServletResponse();

        var result = apiKeyInterceptor.preHandle(request, response, null);

        assertThat(result).isTrue();
    }

    @Test
    void should_throwUnauthorizedClientException_when_apiKeyIsInvalid() {
        var request = new MockHttpServletRequest();
        request.addHeader(API_KEY_HEADER, "test");
        var response = new MockHttpServletResponse();

        assertThatThrownBy(() -> apiKeyInterceptor.preHandle(request, response, null))
                .isInstanceOf(AppException.class)
                .extracting("error")
                .isEqualTo(Error.UNAUTHORIZED_CLIENT);
    }

    @Test
    void should_throwUnauthorizedClientException_when_apiKeyIsMissing() {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        assertThatThrownBy(() -> apiKeyInterceptor.preHandle(request, response, null))
                .isInstanceOf(AppException.class)
                .extracting("error")
                .isEqualTo(Error.UNAUTHORIZED_CLIENT);
    }
}
