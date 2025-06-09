package com.serhiishcherbakov.notebooks.config;

import com.serhiishcherbakov.notebooks.security.ApiKeyInterceptor;
import com.serhiishcherbakov.notebooks.security.UserInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final ApiKeyInterceptor apiKeyInterceptor;
    private final UserInterceptor userInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiKeyInterceptor).addPathPatterns("/notebooks/**", "/tags/**");
        registry.addInterceptor(userInterceptor).addPathPatterns("/notebooks/**", "/tags/**");
    }
}
