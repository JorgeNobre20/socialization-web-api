package com.objectify.socialization.dtos.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateUserInputDto {
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

  @Schema(description = "Imagem de perfil do usuário")
  private MultipartFile image;
}
