package dujardin.thomas.examTestsM1Dujardin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserDto {
    private Long id;

    @NotNull
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull
    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.com$", message = "Email must end with .com")
    private String email;
}
