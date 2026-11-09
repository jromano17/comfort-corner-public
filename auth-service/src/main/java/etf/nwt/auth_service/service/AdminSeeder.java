package etf.nwt.auth_service.service;

import etf.nwt.auth_service.models.User;
import etf.nwt.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${INITIAL_ADMIN_EMAIL:admin@default.com}")
    private String adminEmail;

    @Value("${INITIAL_ADMIN_USERNAME:defaultPassword!}")
    private String adminUsername;

    @Value("${INITIAL_ADMIN_PASSWORD:defaultPassword!}")
    private String adminPassword;

    public AdminSeeder(UserRepository userRepository,  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {

            User adminUser = new User();
            adminUser.setEmail(adminEmail);
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole("ROLE_ADMIN");

            userRepository.save(adminUser);

            System.out.println("✅ Initial Admin user created successfully!");
        } else {
            System.out.println("ℹ️ Admin user already exists. Skipping creation.");
        }
    }
}