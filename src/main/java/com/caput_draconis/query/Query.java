package com.caput_draconis.query;

import com.caput_draconis.domain.domain.InputPassword;
import com.caput_draconis.domain.domain.Password;
import com.caput_draconis.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class Query {
    private final PasswordService passwordService;

    @Autowired
    public Query(PasswordService passwordService){
        this.passwordService = passwordService;
    }

    @QueryMapping
    public List<Password> passwords(){
        return passwordService.getAllPasswords();
    }

    @MutationMapping
    public Password insertPassword(@Argument("passwordInput") InputPassword passwordInput){
        return passwordService.savePassword(passwordInput);
    }

    @MutationMapping
    public Boolean deletePassword(@Argument("uuid") String uuid){
        passwordService.deletePasswordByUuid(uuid);
        return true;
    }
    @QueryMapping
    public Password password(@Argument("uuid") String uuid){
        return passwordService.getPasswordByUuid(uuid);
    }

    @MutationMapping
    public Boolean updatePassword(@Argument("uuid") String uuid , @Argument("password") String password){
        return passwordService.updatePassword(uuid , password);
    }

}
