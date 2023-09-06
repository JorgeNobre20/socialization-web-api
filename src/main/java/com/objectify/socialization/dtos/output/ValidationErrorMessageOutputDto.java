package com.objectify.socialization.dtos.output;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorMessageOutputDto extends InformativeMessageOutputDto {
  private List<ValidationFieldErrorOutputDto> errors;
}
