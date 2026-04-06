package com.ikhlas.finance.controller;

import com.ikhlas.finance.model.User;
import com.ikhlas.finance.service.JwtService;
import com.ikhlas.finance.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveUser(@Valid @RequestBody User user){
        if(userService.userExists(user.getUsername())){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }
        userService.addUser(user);
        return ResponseEntity.ok("User Added Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            if(authentication.isAuthenticated()) {
                UserDetails userDetails=(UserDetails) authentication.getPrincipal();
                String token= jwtService.generateToken(userDetails);
                return ResponseEntity.ok(token);
            }

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or Password");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Authentication Failed");
    }


    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String username){
            userService.deleteUser(username);
            return ResponseEntity.ok("User with " + username + " deleted successfully.");
    }

}
