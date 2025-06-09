package com.serhiishcherbakov.notebooks.security;

import com.serhiishcherbakov.notebooks.config.SecurityProperties;
import com.serhiishcherbakov.notebooks.exception.AppException;
import com.serhiishcherbakov.notebooks.exception.Error;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ApiKeyInterceptor implements HandlerInterceptor {
    private static final String API_KEY_HEADER = "X-Api-Key";

    private final SecurityProperties securityProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var apiKey = request.getHeader(API_KEY_HEADER);

        securityProperties.getClients().stream()
                .filter(c -> c.getKey().equalsIgnoreCase(apiKey))
                .findAny()
                .orElseThrow(() -> new AppException(Error.UNAUTHORIZED_CLIENT));

        return true;
    }
}
