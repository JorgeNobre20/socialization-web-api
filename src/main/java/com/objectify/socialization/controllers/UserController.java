package com.objectify.socialization.controllers;

import com.objectify.socialization.docs.openapi.UserControllerOpenApi;
import com.objectify.socialization.dtos.input.CreateUserInputDto;
import com.objectify.socialization.dtos.input.UpdateUserInputDto;
import com.objectify.socialization.dtos.output.UpdateProfileImageOutputDto;
import com.objectify.socialization.dtos.output.UserOutputDto;
import com.objectify.socialization.exceptions.FileProcessingException;
import com.objectify.socialization.exceptions.ResourceConflictException;
import com.objectify.socialization.exceptions.ResourceNotFoundException;
import com.objectify.socialization.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/users", produces = "application/json")
public class UserController implements UserControllerOpenApi {

  @Autowired
  private UserService userService;

  @GetMapping
  @Override
  public ResponseEntity<List<UserOutputDto>> getAll()  {
    List<UserOutputDto> result = this.userService.getAll();
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  @Override
  public ResponseEntity<UserOutputDto> getProfile(@PathVariable("id") Long userId) throws ResourceNotFoundException, FileProcessingException {
    var user = this.userService.getUserProfile(userId);
    return ResponseEntity.ok(user);
  }

  @PostMapping
  @Override
  public ResponseEntity<Object> create(@Valid @RequestBody CreateUserInputDto input) throws ResourceConflictException {
    this.userService.create(input);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{id}")
  @Override
  public ResponseEntity<Object> delete(@PathVariable("id") Long userId) throws ResourceNotFoundException, FileProcessingException {
    this.userService.delete(userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  @Override
  public ResponseEntity<Object> update(
          @PathVariable("id") Long userId,
          @Valid @ModelAttribute UpdateUserInputDto input
  ) throws ResourceNotFoundException, ResourceConflictException, FileProcessingException {
    this.userService.update(userId, input);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PatchMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  @Override
  public ResponseEntity<UpdateProfileImageOutputDto> updateProfileImage(
        @PathVariable("id") Long userId,
        @RequestPart("image") MultipartFile file
  ) throws ResourceNotFoundException, FileProcessingException {
    var result = this.userService.updateProfileImage(userId, file);
    return ResponseEntity.ok(result);
  }
}
