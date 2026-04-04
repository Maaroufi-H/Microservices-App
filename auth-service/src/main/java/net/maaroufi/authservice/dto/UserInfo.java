package net.maaroufi.authservice.dto;

// Java 21 Record
public record UserInfo(
        Long id,
        String name,
        String surname,
        String email,
        String role
) {}
