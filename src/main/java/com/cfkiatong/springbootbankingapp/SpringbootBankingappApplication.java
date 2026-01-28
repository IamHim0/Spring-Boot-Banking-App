package com.cfkiatong.springbootbankingapp;

import com.cfkiatong.springbootbankingapp.ui.UserInterface;
import com.cfkiatong.springbootbankingapp.ui.UserInterface.*;

import org.apache.catalina.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SpringbootBankingappApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootBankingappApplication.class, args);
    }

}
