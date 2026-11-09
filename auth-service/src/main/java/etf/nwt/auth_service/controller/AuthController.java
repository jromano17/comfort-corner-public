package etf.nwt.auth_service.controller;

import etf.nwt.auth_service.dto.AuthRequest;
import etf.nwt.auth_service.dto.JwtResponse;
import etf.nwt.auth_service.dto.RegisterRequest;
import etf.nwt.auth_service.dto.TokenRefreshRequest;
import etf.nwt.auth_service.models.RefreshToken;
import etf.nwt.auth_service.models.User;
import etf.nwt.auth_service.repository.UserRepository;
import etf.nwt.auth_service.service.JwtService;
import etf.nwt.auth_service.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already in use!");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User(request.getUsername(), hashedPassword, "ROLE_USER", request.getEmail());
        userRepository.save(newUser);

        return login(new AuthRequest(request.getUsername(),request.getPassword()));
        //return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody AuthRequest request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        if (authenticate.isAuthenticated()) {
            Optional<User> user = userRepository.findByUsername(request.getUsername());

            if (user.isEmpty()) {
                throw new RuntimeException("Invalid Access");
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getUsername());
            String accessToken = jwtService.generateToken(user.get().getUsername(), user.get().getRole(), user.get().getId());

            return ResponseEntity.ok(JwtResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .role(user.get().getRole())
                    .username(user.get().getUsername())
                    .build());
        } else {
            throw new RuntimeException("Invalid Access");
        }
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest tokenRefreshRequest) {
        return refreshTokenService.findByToken(tokenRefreshRequest.getToken())
                .map(oldToken -> {
                    String username = oldToken.getUsername();
                    
                    Optional<User> userOptional = userRepository.findByUsername(username);
                    if (userOptional.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("User no longer exists in database!");
                    }
                    User user = userOptional.get();

                    refreshTokenService.deleteRefreshToken(oldToken.getToken());
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(username);
                    String newAccessToken = jwtService.generateToken(user.getUsername(), user.getRole(), user.getId());

                    JwtResponse jwtResponse = JwtResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken.getToken())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .build();
                    return ResponseEntity.ok(jwtResponse);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Refresh token is invalid or expired!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody TokenRefreshRequest request) {
        refreshTokenService.deleteRefreshToken(request.getToken());
        return ResponseEntity.ok("Successfully logged out and token destroyed!");
    }
}