package com.example.demo;

import java.security.Principal;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class UserController {
	
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/login")
    public boolean login(@RequestBody User user) {
        return
          user.getUsername().equals("demouser") && user.getPassword().equals("password");
    }
	
    @GetMapping("/user")
    public Principal user(HttpServletRequest request) {
    	log.info("getUser called, {} ", request.toString());
        String authToken = request.getHeader("Authorization")
          .substring("Basic".length()).trim();
        return () ->  new String(Base64.getDecoder()
          .decode(authToken)).split(":")[0];
    }
}
