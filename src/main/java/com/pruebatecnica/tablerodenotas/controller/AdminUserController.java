package com.pruebatecnica.tablerodenotas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pruebatecnica.tablerodenotas.entidades.User;
import com.pruebatecnica.tablerodenotas.seguridad.UserAdminService;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserAdminService userAdminService;

    public AdminUserController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    public record UserRequest(String name, String email, String password, String role, Boolean active) {
    }

    public record StatusRequest(boolean active) {
    }

    public record UserResponse(Long id, String name, String email, String role, boolean active) {
        static UserResponse from(User user) {
            return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isActive());
        }
    }

    @GetMapping
    public List<UserResponse> list() {
        return userAdminService.findAll().stream().map(UserResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody UserRequest request) {
        return UserResponse.from(userAdminService.create(
                request.name(), request.email(), request.password(), request.role(),
                request.active() == null || request.active()));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody UserRequest request) {
        return UserResponse.from(userAdminService.update(
                id, request.name(), request.email(), request.password(), request.role(), request.active()));
    }

    @PatchMapping("/{id}/status")
    public UserResponse setStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        return UserResponse.from(userAdminService.setActive(id, request.active()));
    }
}