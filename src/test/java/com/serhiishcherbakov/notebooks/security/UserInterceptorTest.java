package com.serhiishcherbakov.notebooks.security;

import com.serhiishcherbakov.notebooks.exception.AppException;
import com.serhiishcherbakov.notebooks.exception.Error;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {UserInterceptor.class})
class UserInterceptorTest {
    private static final String USER_DETAILS_ATTRIBUTE = "userDetails";
    private static final String USER_AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    private UserInterceptor userInterceptor;

    @Test
    void should_extractUserDetailsAndAddToRequestAttributes() {
        var expectedUserDetails = new UserDetails("1", "Test User", "test.user@email.com");

        var request = new MockHttpServletRequest();
        request.addHeader(USER_AUTHORIZATION_HEADER, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwibmFtZSI6IlRlc3QgVXNlciIsImVtYWlsIjoidGVzdC51c2VyQGVtYWlsLmNvbSJ9.2eVA2QN3O1w3lFSGtr5vIUkZAUobmR9ZSER4cmInsbU");
        var response = new MockHttpServletResponse();

        var result = userInterceptor.preHandle(request, response, null);

        assertThat(result).isTrue();
        assertThat(request.getAttribute(USER_DETAILS_ATTRIBUTE))
                .isNotNull()
                .isEqualTo(expectedUserDetails);
    }

    @Test
    void should_throwUnauthorizedUserException_when_authorizationHeaderIsMissing() {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        assertThatThrownBy(() -> userInterceptor.preHandle(request, response, null))
                .isInstanceOf(AppException.class)
                .extracting("error")
                .isEqualTo(Error.UNAUTHORIZED_USER);
    }

    @Test
    void should_throwUnauthorizedUserException_when_tokenIsInvalid() {
        var request = new MockHttpServletRequest();
        request.addHeader(USER_AUTHORIZATION_HEADER, "Bearer invalid_token");
        var response = new MockHttpServletResponse();

        assertThatThrownBy(() -> userInterceptor.preHandle(request, response, null))
                .isInstanceOf(AppException.class)
                .extracting("error")
                .isEqualTo(Error.UNAUTHORIZED_USER);
    }
}
