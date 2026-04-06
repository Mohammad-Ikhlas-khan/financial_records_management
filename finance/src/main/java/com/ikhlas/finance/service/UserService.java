package com.ikhlas.finance.service;

import com.ikhlas.finance.exception.ResourceNotFoundException;
import com.ikhlas.finance.model.Role;
import com.ikhlas.finance.model.User;
import com.ikhlas.finance.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);

    public boolean userExists(String username){
        User user=userRepo.findByUsername(username);
        return user != null;
    }

    public void addUser(User user){
        user.setRole(Role.valueOf(user.getRole().name().toUpperCase()));
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
    }

    public void deleteUser(String username){
        User user= Optional.ofNullable(userRepo.findByUsername(username))
                .orElseThrow(()->new ResourceNotFoundException("User not found"));
        userRepo.deleteById(user.getUserId());
    }
}
