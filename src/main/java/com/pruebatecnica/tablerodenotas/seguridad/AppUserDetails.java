package com.pruebatecnica.tablerodenotas.seguridad;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pruebatecnica.tablerodenotas.Repo.UserRepo;
import com.pruebatecnica.tablerodenotas.entidades.User;

@Service
public class AppUserDetails implements UserDetailsService {

     private final UserRepo userRepo;

    public AppUserDetails(UserRepo userRepo) {
        this.userRepo = userRepo;

        
    }

    

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
}
