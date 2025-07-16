package com.caput_draconis.repository;

import com.caput_draconis.domain.entity.PasswordEntity;
import com.caput_draconis.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PasswordRepository extends JpaRepository<PasswordEntity , String> {
    @Query("SELECT password FROM PasswordEntity password WHERE password.isDeleted != :isActive AND password.uname = :userEntity ORDER BY password.domain_name")
    List<PasswordEntity> findAllActiveOrTrashPasswords(Boolean isActive, UserEntity userEntity);
    @Modifying
    @Query("UPDATE PasswordEntity password SET password.password = :password , password.updated_at = CURRENT_TIMESTAMP WHERE password.uuid = :uuid")
    int updatePasswordEntityByUuid(@Param("uuid")String uuid , @Param("password") String password);

    @Modifying
    @Query("UPDATE PasswordEntity password SET password.isDeleted = true , password.updated_at = CURRENT_TIMESTAMP WHERE password.uuid = :uuid")
    int movePasswordToTrash(@Param("uuid")String uuid);

    @Modifying
    @Query(value = "DELETE FROM passwords WHERE is_deleted = true AND updated_at < (CURRENT_TIMESTAMP - INTERVAL '30 days')", nativeQuery = true)
    void deletePasswordsFromTrash();

    @Modifying
    @Query("UPDATE PasswordEntity password SET password.isDeleted = false , password.updated_at = CURRENT_TIMESTAMP WHERE password.uuid = :uuid")
    int restorePassword(@Param("uuid") String uuid);

    PasswordEntity getReferenceByUuidAndUname(String uuid, UserEntity uname);

}
