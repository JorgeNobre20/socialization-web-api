package com.objectify.socialization.exceptions;

import com.objectify.socialization.dtos.output.InformativeMessageOutputDto;
import com.objectify.socialization.dtos.output.ValidationFieldErrorOutputDto;
import com.objectify.socialization.dtos.output.ValidationErrorMessageOutputDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
  private final MessageSource messageSource;

  @ExceptionHandler(FileProcessingException.class)
  public ResponseEntity<InformativeMessageOutputDto> handleFileProcessingException(FileProcessingException exception){
    var informativeMessage = new InformativeMessageOutputDto();
    informativeMessage.setMessage(exception.getMessage());

    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(informativeMessage);
  }

  @ExceptionHandler(ResourceConflictException.class)
  public ResponseEntity<InformativeMessageOutputDto> handleBusinessException(ResourceConflictException exception){
    var informativeMessage = new InformativeMessageOutputDto();
    informativeMessage.setMessage(exception.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(informativeMessage);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<InformativeMessageOutputDto> handleResourceNotFoundException(ResourceNotFoundException exception){
    var informativeMessage = new InformativeMessageOutputDto();
    informativeMessage.setMessage(exception.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(informativeMessage);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
    @NonNull MethodArgumentNotValidException exception,
    @NonNull HttpHeaders headers,
    @NonNull HttpStatusCode status,
    @NonNull WebRequest request
  ){
    var validationErrorMessage = new ValidationErrorMessageOutputDto();
    List<ValidationFieldErrorOutputDto> fieldErrorList = this.mapBindExceptionToFieldError(exception);

    validationErrorMessage.setMessage("Um ou mais campos inválidos");
    validationErrorMessage.setErrors(fieldErrorList);

    return this.handleExceptionInternal(exception, validationErrorMessage, headers, HttpStatus.BAD_REQUEST, request);
  }

  private List<ValidationFieldErrorOutputDto> mapBindExceptionToFieldError(BindException exception) {
    List<ValidationFieldErrorOutputDto> fieldErrorsList = new ArrayList<ValidationFieldErrorOutputDto>();

    for(ObjectError error : exception.getBindingResult().getAllErrors()) {
      var fieldName = ((FieldError) error).getField();
      var errorDescription = this.messageSource.getMessage(error, LocaleContextHolder.getLocale());

      var validationFieldError = new ValidationFieldErrorOutputDto();

      validationFieldError.setFieldName(fieldName);
      validationFieldError.setErrorDescription(errorDescription);

      fieldErrorsList.add(validationFieldError);
    }

    return fieldErrorsList;
  }
}
