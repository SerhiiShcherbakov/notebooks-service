package com.serhiishcherbakov.notebooks.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetails {
    private final String id;
    private final String fullName;
    private final String email;
}
