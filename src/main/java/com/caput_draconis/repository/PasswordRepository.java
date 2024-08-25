package com.caput_draconis.repository;

import com.caput_draconis.domain.entity.PasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordRepository extends JpaRepository<PasswordEntity , String> {
    List<PasswordEntity> findAll();
}
