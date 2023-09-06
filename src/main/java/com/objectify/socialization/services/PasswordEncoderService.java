package com.objectify.socialization.services;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoderService {
  public String encode(String password){
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }
}
