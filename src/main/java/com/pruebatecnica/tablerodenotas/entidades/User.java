package com.pruebatecnica.tablerodenotas.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor 
@Entity 
@Table (name = "users")
@Data 
@AllArgsConstructor 
@Builder 

public class User {
    int id;
    String name;
    String email;
    String password;
    String role;
    boolean active;
}
