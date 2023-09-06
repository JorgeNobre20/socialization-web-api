package com.objectify.socialization.docs.openapi;

import com.objectify.socialization.dtos.input.CreateUserInputDto;
import com.objectify.socialization.dtos.input.UpdateUserInputDto;
import com.objectify.socialization.dtos.output.InformativeMessageOutputDto;
import com.objectify.socialization.dtos.output.UpdateProfileImageOutputDto;
import com.objectify.socialization.dtos.output.UserOutputDto;
import com.objectify.socialization.dtos.output.ValidationErrorMessageOutputDto;
import com.objectify.socialization.exceptions.FileProcessingException;
import com.objectify.socialization.exceptions.ResourceConflictException;
import com.objectify.socialization.exceptions.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Usuários", description = "Rotas que lidam com as operações de usuários")
public interface UserControllerOpenApi {

  @Operation(
          summary = "Lista todos os usuário existentes",
          description = "Lista todos os usuários que estão cadastrados no sistema"
  )
  @ApiResponse(
          responseCode = "200",
          description = "Usuários listados com sucesso"
  )
  public ResponseEntity<List<UserOutputDto>> getAll();

  @Operation(
          summary = "Obtém dados do perfil do usuário",
          description = "Retorna o perfil do usuário a partir do id"
  )
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "404",
                          description = "Não existe um usuário com id informado",
                          content = {@Content(schema = @Schema(implementation = InformativeMessageOutputDto.class))}
                  ),
                  @ApiResponse(
                          responseCode = "200",
                          description = "Dados do perfil do usuário encontrados com sucesso",
                          content = {@Content(schema = @Schema(implementation = UserOutputDto.class))},
                          useReturnTypeSchema = true
                  )
          }

  )
  public ResponseEntity<UserOutputDto> getProfile(Long userId) throws ResourceNotFoundException, FileProcessingException;


  @Operation(
          summary = "Cria um novo usuário",
          description = "Faz o cadastro de um novo usuário"
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "204",
                  description = "Usuário cadastrado com sucesso",
                  content = {@Content(schema = @Schema(implementation = void.class))}
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Corpo da requisição com informações inválidas",
                  content = {@Content(schema = @Schema(implementation = ValidationErrorMessageOutputDto.class))}
          ),
          @ApiResponse(
                  responseCode = "409",
                  description = "Já existe um usuário cadastrado com o email informado",
                  content = {@Content(schema = @Schema(implementation = InformativeMessageOutputDto.class))}
          )
  })
  public ResponseEntity<Object> create(CreateUserInputDto input) throws ResourceConflictException;

  @Operation(
          summary = "Remove um usuário existente",
          description = "Remove o usuário cujo id foi passado na rota"
  )
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "200",
                          description = "Usuário deletado com sucesso"
                  ),
                  @ApiResponse(
                          responseCode = "404",
                          description = "Não existe um usuário com o id informado",
                          content = {@Content(schema = @Schema(implementation = InformativeMessageOutputDto.class))}
                  )
          }
  )
  public ResponseEntity<Object> delete(Long userId) throws ResourceNotFoundException, FileProcessingException;

  @Operation(
          summary = "Atualiza dados do usuário",
          description = "Atualiza dados de um usuário previamente cadastrado no sistema"
  )
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "200",
                          description = "Atualização feita com sucesso"
                  ),
                  @ApiResponse(
                          responseCode = "400",
                          description = "Corpo da requisição com informações inválidas",
                          content = {@Content(schema = @Schema(implementation = ValidationErrorMessageOutputDto.class))}
                  ),
                  @ApiResponse(
                          responseCode = "404",
                          description = "Não existe um usuário com o id informado",
                          content = {@Content(schema = @Schema(implementation = InformativeMessageOutputDto.class))}
                  ),
                  @ApiResponse(
                          responseCode = "409",
                          description = "Já existe um usuário cadastrado com o email informado",
                          content = {@Content(schema = @Schema(implementation = InformativeMessageOutputDto.class))}
                  ),
                  @ApiResponse(
                          responseCode = "422",
                          description = "O arquivo de imagem enviado não pode ser processado"
                  )
          }
  )
  public ResponseEntity<Object> update(
          Long userId,
          UpdateUserInputDto input
  ) throws ResourceNotFoundException, ResourceConflictException, FileProcessingException;

  @Operation(
          summary = "Atualiza foto de perfil do usuário",
          description = "Atualiza foto de perfil do usuário cujo id foi passado"
  )
  @ApiResponses(
          value = {
                  @ApiResponse(
                          responseCode = "200",
                          description = "Atualização de foto feita com sucesso"
                  ),
                  @ApiResponse(
                          responseCode = "422",
                          description = "O arquivo de imagem enviado não pode ser processado"
                  )
          }
  )
  public ResponseEntity<UpdateProfileImageOutputDto> updateProfileImage(
          Long userId,
          MultipartFile file
  ) throws ResourceNotFoundException, FileProcessingException;
}
