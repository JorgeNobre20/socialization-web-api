package com.objectify.socialization.services;

import com.objectify.socialization.dtos.input.CreateUserInputDto;
import com.objectify.socialization.dtos.input.UpdateUserInputDto;
import com.objectify.socialization.dtos.output.UpdateProfileImageOutputDto;
import com.objectify.socialization.dtos.output.UserOutputDto;
import com.objectify.socialization.exceptions.FileProcessingException;
import com.objectify.socialization.exceptions.ResourceConflictException;
import com.objectify.socialization.exceptions.ResourceNotFoundException;
import com.objectify.socialization.models.UserModel;
import com.objectify.socialization.repositories.UserRepository;
import com.objectify.socialization.services.interfaces.IFileStorageService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private IFileStorageService storageService;

  @Autowired
  private PasswordEncoderService passwordEncoderService;

  @Autowired
  private ModelMapper modelMapper;

  @Transactional
  public void create(CreateUserInputDto input) throws ResourceConflictException {
    Optional<UserModel> existingUser = this.userRepository.findByEmail(input.getEmail());

    if(existingUser.isPresent()){
      String message = String.format("Já existe um usuário cadastrado com o e-mail: %s", input.getEmail());
      throw new ResourceConflictException(message);
    }

    UserModel newUser = this.modelMapper.map(input, UserModel.class);

    String encodedPassword = this.passwordEncoderService.encode(input.getPassword());
    newUser.setPassword(encodedPassword);

    this.userRepository.save(newUser);
  }

  public UserOutputDto getUserProfile(Long id) throws ResourceNotFoundException, FileProcessingException {
    UserModel userModel = this.getById(id);
    UserOutputDto userOutputDto = new UserOutputDto();

    userOutputDto.setId(userModel.getId());
    userOutputDto.setName(userModel.getName());
    userOutputDto.setEmail(userModel.getEmail());
    userOutputDto.setImageUrl(userModel.getImageUrl());

    return userOutputDto;
  }

  public UserModel getById(Long id) throws ResourceNotFoundException {
    Optional<UserModel> existingUser = this.userRepository.findById(id);

    if(existingUser.isEmpty()){
      String message = String.format("Não existe um usuário com ID: %s", id);
      throw new ResourceNotFoundException(message);
    }

    return existingUser.get();
  }

  @Transactional
  public void delete(Long userId) throws ResourceNotFoundException, FileProcessingException {
    UserModel user = this.getById(userId);
    this.userRepository.delete(user);
    this.storageService.delete(user.getImageUrl());
  }

  @Transactional
  public void update(Long userId, UpdateUserInputDto input) throws ResourceNotFoundException, ResourceConflictException, FileProcessingException {
    UserModel user = this.getById(userId);
    this.checkEmailInUserByAnotherUser(input.getEmail(), userId);

    this.modelMapper.map(input, user);

    String encodedPassword = this.passwordEncoderService.encode(input.getPassword());
    user.setPassword(encodedPassword);

    if(Objects.nonNull(input.getImage())){
      this.updateProfileImage(userId, input.getImage());
    }

    this.userRepository.save(user);
  }

  private void checkEmailInUserByAnotherUser(String email, Long userIdToComparison) throws ResourceConflictException{
    Optional<UserModel> userWithSameEmailAndDifferentId = this.userRepository.findByEmailAndIdNot(email, userIdToComparison);

    if(userWithSameEmailAndDifferentId.isPresent()){
      String message = "O email informado já está sendo utilizado por outro usuário";
      throw new ResourceConflictException(message);
    }
  }

  @Transactional
  public UpdateProfileImageOutputDto updateProfileImage(
          Long userId,
          MultipartFile file
  ) throws ResourceNotFoundException, FileProcessingException {
    UserModel user = this.getById(userId);

    String newImageUrl = this.storageService.save("/", file);
    this.deleteOldUserProfileImage(user.getImageUrl());

    user.setImageUrl(newImageUrl);
    this.userRepository.save(user);

    return new UpdateProfileImageOutputDto(newImageUrl);
  }

  private void deleteOldUserProfileImage(String userImageUrl) throws FileProcessingException {
    if(Objects.nonNull(userImageUrl)){
      this.storageService.delete(userImageUrl);
    }
  }

  public List<UserOutputDto> getAll(){
    List<UserModel> userEntities = this.userRepository.findAll();
    List<UserOutputDto> outputUsers = new ArrayList<UserOutputDto>();

    userEntities.forEach(userEntity -> {
      var userOutputDto = this.modelMapper.map(userEntity, UserOutputDto.class);
      outputUsers.add(userOutputDto);
    });

    return outputUsers;
  }
}
