package etf.nwt.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "User username is required")
    @NotEmpty
    @Size(max = 200)
    private String username;

    @NotBlank(message = "User password is required")
    @NotEmpty
    @Size(max = 200)
    private String password;

    private String role = "ROLE_USER";


    @NotBlank(message = "User email is required")
    @NotEmpty
    @Email
    @Size(max = 200)
    private String email;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}