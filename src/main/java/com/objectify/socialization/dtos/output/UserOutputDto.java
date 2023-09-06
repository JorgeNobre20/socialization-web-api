package com.objectify.socialization.dtos.output;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserOutputDto {
  @Schema(description = "Id do usuário", example = "1")
  private Long id;

  @Schema(description = "Nome do usuário", example = "José")
  private String name;

  @Schema(description = "Email do usuário", example = "jose@email.com")
  private String email;

  @Schema(
          description = "URL da foto de perfil do usuário",
          example = "http://localhost:8080/static/image.html",
          nullable = true
  )
  private String imageUrl;
}
