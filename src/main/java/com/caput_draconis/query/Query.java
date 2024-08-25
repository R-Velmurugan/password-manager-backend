package com.caput_draconis.query;

import com.caput_draconis.domain.domain.Password;
import com.caput_draconis.domain.entity.PasswordEntity;
import com.caput_draconis.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class Query {
    private PasswordService passwordService;

    @Autowired
    public Query(PasswordService passwordService){
        this.passwordService = passwordService;
    }

    @QueryMapping
    public List<Password> passwords(){
        return passwordService.getAllPasswords();
    }
}
