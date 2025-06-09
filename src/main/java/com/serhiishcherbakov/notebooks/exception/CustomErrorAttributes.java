package com.serhiishcherbakov.notebooks.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> attributes = super.getErrorAttributes(webRequest, options);
        Throwable error = getError(webRequest);

        if (error instanceof BindingResult bindingResult && bindingResult.hasErrors()) {
            Map<String, String> validationErrors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            org.springframework.validation.FieldError::getField,
                            org.springframework.validation.FieldError::getDefaultMessage
                    ));

            attributes.put("message", "Validation error");
            attributes.put("errors", validationErrors);
        }

        return attributes;
    }
}
