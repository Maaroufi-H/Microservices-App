package net.maaroufi.authservice.services;

import net.maaroufi.authservice.dto.*;
import net.maaroufi.authservice.entities.AppUser;
import net.maaroufi.authservice.enums.Role;
import net.maaroufi.authservice.exceptions.AuthException;
import net.maaroufi.authservice.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager,
                       UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException("Un compte existe deja avec cet email: " + request.email());
        }

        var user = new AppUser();
        user.setName(request.name());
        user.setSurname(request.surname());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setAge(request.age());
        user.setGender(request.gender());
        user.setCountry(request.country());
        user.setCreatedAt(LocalDateTime.now());

        var saved = userRepository.save(user);
        var userDetails = userDetailsService.loadUserByUsername(saved.getEmail());
        var token = jwtService.generateToken(userDetails);

        return new LoginResponse(token, toUserInfo(saved));
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new AuthException("Email ou mot de passe incorrect");
        }

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException("Utilisateur introuvable"));
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var token = jwtService.generateToken(userDetails);

        // Java 21 pattern matching switch
        String welcomeMessage = switch (user.getRole()) {
            case ADMIN -> "Bienvenue administrateur " + user.getName();
            case USER  -> "Bienvenue sur WellnessShop, " + user.getName();
        };
        System.out.println(welcomeMessage);

        return new LoginResponse(token, toUserInfo(user));
    }

    @Transactional(readOnly = true)
    public UserInfo getMe(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Utilisateur introuvable"));
        return toUserInfo(user);
    }

    private UserInfo toUserInfo(AppUser user) {
        return new UserInfo(user.getId(), user.getName(), user.getSurname(),
                user.getEmail(), user.getRole().name());
    }
}
