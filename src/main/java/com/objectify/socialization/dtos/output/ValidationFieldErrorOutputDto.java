package com.objectify.socialization.dtos.output;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationFieldErrorOutputDto {
  private String fieldName;
  private String errorDescription;
}
