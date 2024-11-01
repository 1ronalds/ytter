package api.ytter.backend.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@Getter
public class RegistrationData {
    @NotBlank
    private String username;
    @NotBlank
    @NotBlank
    private String name;
    @Email
    private String email;
    @NotBlank
    @Length(min=8)
    private String password;
}
