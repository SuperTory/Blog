package com.tory.blog.config;

import org.assertj.core.api.Assert;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    StringEncryptor encryptor;

    @Test
    public void getPass() {
        String url = encryptor.encrypt("jdbc:mysql://39.99.152.76:3306/blog");
        String name = encryptor.encrypt("work");
        String password = encryptor.encrypt("Work1234!");
        System.out.println("jdbc:"+url);
        System.out.println("username:"+name);
        System.out.println("password:"+password);
    }
}