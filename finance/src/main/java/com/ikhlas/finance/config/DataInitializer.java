package com.ikhlas.finance.config;

import com.ikhlas.finance.model.Role;
import com.ikhlas.finance.model.User;
import com.ikhlas.finance.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);

    @Override
    public void run(String... args){
        if(userRepo.findByUsername("admin")==null){
            User admin=new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);

            userRepo.save(admin);
            System.out.println("Default Admin Created!");
        }
    }
}
