package com.caput_draconis.repository;

import com.caput_draconis.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity , String> {
    List<UserEntity> findByUsername(String username);
}
