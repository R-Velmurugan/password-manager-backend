package com.caput_draconis.repository;

import com.caput_draconis.domain.entity.PasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PasswordRepository extends JpaRepository<PasswordEntity , String> {
    @Modifying
    @Query("UPDATE PasswordEntity password SET password.password = :password , password.updated_at = CURRENT_TIMESTAMP WHERE password.uuid = :uuid")
    int updatePasswordEntityByUuid(@Param("uuid")String uuid , @Param("password") String password);
}
