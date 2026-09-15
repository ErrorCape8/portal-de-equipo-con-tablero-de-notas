package com.pruebatecnica.tablerodenotas.seguridad;

import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pruebatecnica.tablerodenotas.Repo.UserRepo;
import com.pruebatecnica.tablerodenotas.entidades.User;

@Service
public class UserAdminService {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserAdminService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Transactional
    public User create(String name, String email, String password, String role, boolean active) {
        String normalizedName = required(name, "El nombre es obligatorio");
        String normalizedEmail = required(email, "El email es obligatorio").toLowerCase(Locale.ROOT);
        String normalizedRole = normalizeRole(role);

        if (userRepo.existsByName(normalizedName)) {
            throw new IllegalArgumentException("El nombre ya esta registrado");
        }
        if (userRepo.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("El email ya esta registrado");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (!active && userRepo.countByRoleIgnoreCaseAndActiveTrue(ADMIN_ROLE) == 0) {
            throw new IllegalArgumentException("Debe existir al menos un administrador activo");
        }

        return userRepo.save(User.builder()
                .name(normalizedName)
                .email(normalizedEmail)
                .password(passwordEncoder.encode(password))
                .role(normalizedRole)
                .active(active)
                .build());
    }

    @Transactional
    public User update(Long id, String name, String email, String password, String role, Boolean active) {
        User user = findById(id);
        String normalizedName = required(name, "El nombre es obligatorio");
        String normalizedEmail = required(email, "El email es obligatorio").toLowerCase(Locale.ROOT);
        String normalizedRole = normalizeRole(role);

        if (userRepo.existsByNameAndIdNot(normalizedName, id)) {
            throw new IllegalArgumentException("El nombre ya esta registrado");
        }
        if (userRepo.existsByEmailAndIdNot(normalizedEmail, id)) {
            throw new IllegalArgumentException("El email ya esta registrado");
        }

        boolean nextActive = active == null ? user.isActive() : active;
        ensureAdminInvariant(user, normalizedRole, nextActive);

        user.setName(normalizedName);
        user.setEmail(normalizedEmail);
        user.setRole(normalizedRole);
        user.setActive(nextActive);
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        return userRepo.save(user);
    }

    @Transactional
    public User setActive(Long id, boolean active) {
        User user = findById(id);
        ensureAdminInvariant(user, user.getRole(), active);
        user.setActive(active);
        return userRepo.save(user);
    }

    private void ensureAdminInvariant(User user, String nextRole, boolean nextActive) {
        if (ADMIN_ROLE.equals(nextRole) && user.isActive() && !nextActive
                && userRepo.countByRoleIgnoreCaseAndActiveTrue(ADMIN_ROLE) <= 1) {
            throw new IllegalArgumentException("Debe existir al menos un administrador activo");
        }
        if (ADMIN_ROLE.equals(user.getRole()) && user.isActive()
                && (!ADMIN_ROLE.equals(nextRole) || !nextActive)
                && userRepo.countByRoleIgnoreCaseAndActiveTrue(ADMIN_ROLE) <= 1) {
            throw new IllegalArgumentException("Debe existir al menos un administrador activo");
        }
    }

    private User findById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private String normalizeRole(String role) {
        String normalizedRole = required(role, "El rol es obligatorio").toUpperCase(Locale.ROOT);
        if (!ADMIN_ROLE.equals(normalizedRole) && !USER_ROLE.equals(normalizedRole)) {
            throw new IllegalArgumentException("El rol debe ser ADMIN o USER");
        }
        return normalizedRole;
    }

    private String required(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}