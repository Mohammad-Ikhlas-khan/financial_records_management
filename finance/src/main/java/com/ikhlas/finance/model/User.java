package com.ikhlas.finance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Username is required.")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min=8,message = "Password must be at least 8 characters")
    private String password;


    @Column(name="is_active")
    private Boolean active=true;


    @NotNull(message="Role is required.")
    @Enumerated(EnumType.STRING)
    private Role role;

}
