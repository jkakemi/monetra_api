package com.finance.api.domain.user;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private Cpf cpf;
    private String name;
    private Email email;
    private String password;
    private UserRole role;

    public User(String cpf, String name, String email, String password, UserRole role) {
        this.cpf = new Cpf(cpf);
        this.name = name;
        this.email = new Email(email);

        this.password = password;
        this.role = (role != null) ? role : UserRole.USER;
    }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        this.name = name;
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Invalid password");
        }
        this.password = password;
    }

    public UserRole getRole() { return role; }
    public void setId(Long id) { this.id = id;}
    public Long getId() { return id; }
    public String getCpf() { return cpf.getNumber(); }
    public String getName() { return name; }
    public String getEmail() { return email.getAddress(); }
    public String getPassword() { return password; }
}
