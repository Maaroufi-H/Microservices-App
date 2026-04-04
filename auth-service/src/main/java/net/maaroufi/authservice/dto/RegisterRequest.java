package net.maaroufi.authservice.dto;

import jakarta.validation.constraints.*;

// Java 21 Record
public record RegisterRequest(
        @NotBlank String name,
        @NotBlank String surname,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 6) String password,
        @Min(1) @Max(120) int age,
        String gender,
        String country
) {}
