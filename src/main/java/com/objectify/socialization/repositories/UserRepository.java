package com.objectify.socialization.repositories;

import com.objectify.socialization.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
  Optional<UserModel> findByEmail(String email);
  Optional<UserModel> findByEmailAndIdNot(String email, Long id);
}
