package com.objectify.socialization.services.interfaces;

import java.util.Objects;
import java.util.UUID;

import com.objectify.socialization.exceptions.FileProcessingException;
import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {
  String save(String directory, MultipartFile file) throws FileProcessingException;

  void delete(String resourceUrl) throws FileProcessingException;

  default String generateFileName(String originalFileName) {
    String trimmedOriginalFilename = Objects.requireNonNull(originalFileName).replaceAll(" ", "-");
    return UUID.randomUUID().toString() + "-" + trimmedOriginalFilename;
  }
}
