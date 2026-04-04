package net.maaroufi.authservice;

import net.maaroufi.authservice.entities.AppUser;
import net.maaroufi.authservice.enums.Role;
import net.maaroufi.authservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    // Seed un admin par defaut au demarrage (H2 only)
    @Bean
    CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@wellness.fr").isEmpty()) {
                var admin = new AppUser();
                admin.setName("Admin");
                admin.setSurname("WellnessShop");
                admin.setEmail("admin@wellness.fr");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setAge(30);
                admin.setGender("M");
                admin.setCountry("FR");
                admin.setCreatedAt(LocalDateTime.now());
                userRepository.save(admin);
            }
        };
    }
}
