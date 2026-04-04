package net.maaroufi.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Java 21 Record - DTO immutable compact
public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
