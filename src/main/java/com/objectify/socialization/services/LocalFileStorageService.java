package com.objectify.socialization.services;

import com.objectify.socialization.exceptions.FileProcessingException;
import com.objectify.socialization.services.interfaces.IFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class LocalFileStorageService implements IFileStorageService {
  @Autowired
  private ServletWebServerApplicationContext webServerAppContext;

  @Override
  public String save(String directory, MultipartFile file) throws FileProcessingException {
    ArrayList<String> uploadDirectoryPathParts = this.getUploadFolderPathParts(directory);
    var uploadDirectoryPathPartsArray = Arrays.copyOf(uploadDirectoryPathParts.toArray(), uploadDirectoryPathParts.size(), String[].class);

    Path uploadPath = Paths.get("src", uploadDirectoryPathPartsArray);
    this.createDirectoryIfNotExists(uploadPath);

    try {
      return this.trySave(uploadPath, file);
    } catch (IOException exception) {
      throw new FileProcessingException(exception.getMessage());
    }
  }

  private String trySave(Path path, MultipartFile file) throws IOException {
    InputStream inputStream = file.getInputStream();
    String newFileName = this.generateFileName(file.getOriginalFilename());

    Path filePath = path.resolve(newFileName);
    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

    String storedFilename = filePath.getFileName().toString();
    return this.getFileUrl(storedFilename);
  }

  private String getFileUrl(String filename) {
    String serverUrl = this.getServerUrl();
    String standardizeDirectory = this.standardizeDirectoryPath("/");

    return serverUrl.concat(standardizeDirectory).concat(filename);
  }

  private void createDirectoryIfNotExists(Path directory) throws FileProcessingException {
    try {
      if (!Files.exists(directory)) {
        Files.createDirectories(directory);
      }
    } catch (IOException exception) {
      throw new FileProcessingException("Erro ao criar pasta para salvar arquivo");
    }
  }

  @Override
  public void delete(String resourceUrl) throws FileProcessingException {
    try {
      this.tryDelete(resourceUrl);
    }catch (IOException exception){
      throw new FileProcessingException("Erro ao remover arquivo");
    }
  }

  private void tryDelete(String resourceUrl) throws IOException {
    Path filePath = this.getCompleteStoragedFilePath(resourceUrl);
    Files.delete(filePath);
  }

  private Path getCompleteStoragedFilePath(String resourceUrl){
    ArrayList<String> uploadDirectoryPathParts = this.getUploadFolderPathParts("");

    String serverUrl = this.getServerUrl();
    String localFileLocation = resourceUrl.replace(serverUrl, "").replace("/public", "");

    String[] localFileLocationPath = localFileLocation.split("/");
    uploadDirectoryPathParts.addAll(List.of(localFileLocationPath));

    var uploadDirectoryPathPartsArray = Arrays.copyOf(uploadDirectoryPathParts.toArray(), uploadDirectoryPathParts.size(), String[].class);
    return Paths.get("src", uploadDirectoryPathPartsArray);
  }

  private ArrayList<String> getUploadFolderPathParts(String directory){
    String standardizedDirectory = this.standardizeDirectoryPath(directory);
    String[] directoryPathParts = standardizedDirectory.split("/");
    ArrayList<String> uploadFolderPathParts = new ArrayList<String>();

    uploadFolderPathParts.add("main");
    uploadFolderPathParts.add("resources");
    uploadFolderPathParts.add("public");

    Collections.addAll(uploadFolderPathParts, directoryPathParts);
    return uploadFolderPathParts;
  }

  private String standardizeDirectoryPath(String directory){
    String standardizedDirectoryPath = directory;

    if(Objects.isNull(directory) || directory.isEmpty()){
      return directory;
    }

    if(directory.charAt(0) != '/'){
      standardizedDirectoryPath = "/".concat(directory);
    }

    int lastStringCharacterIndex = directory.length() - 1;

    if(directory.charAt(lastStringCharacterIndex) != '/'){
      standardizedDirectoryPath = standardizedDirectoryPath.concat("/");
    }

    return standardizedDirectoryPath;
  }

  private String getServerUrl(){
    final int SERVER_PORT = this.webServerAppContext.getWebServer().getPort();
    return "http://localhost:".concat(String.valueOf(SERVER_PORT));
  }
}
