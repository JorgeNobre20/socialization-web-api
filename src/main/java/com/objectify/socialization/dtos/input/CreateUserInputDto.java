package com.objectify.socialization.dtos.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CreateUserInputDto {
  @NotBlank
  @Size(min = 5, max = 100)
  @Schema(description = "Nome do usuário", example = "José")
  private String name;

  @Email
  @NotBlank
  @Schema(description = "Email do usuário", example = "jose@email.com")
  private String email;

  @NotBlank
  @Schema(description = "Senha do usuário", example = "jose12345")
  private String password;
}
