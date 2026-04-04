package net.maaroufi.authservice.dto;

// Java 21 Record
public record LoginResponse(
        String token,
        String tokenType,
        UserInfo user
) {
    // Constructeur compact avec tokenType par defaut
    public LoginResponse(String token, UserInfo user) {
        this(token, "Bearer", user);
    }
}
