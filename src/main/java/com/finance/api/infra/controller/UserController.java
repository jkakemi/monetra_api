package com.finance.api.infra.controller;

import com.finance.api.application.usecases.user.CreateUser;
import com.finance.api.application.usecases.user.DeleteUser;
import com.finance.api.application.usecases.user.GetUserProfile;
import com.finance.api.application.usecases.user.UpdateUser;
import com.finance.api.domain.user.User;
import com.finance.api.infra.controller.dto.UserResponseDTO;
import com.finance.api.infra.controller.dto.UserUpdateDTO;
import com.finance.api.infra.persistence.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUser createUser;
    private final GetUserProfile getUserProfile;
    private final UpdateUser updateUser;
    private final DeleteUser deleteUser;

    public UserController(CreateUser createUser, GetUserProfile getUserProfile,
                          UpdateUser updateUser, DeleteUser deleteUser) {
        this.createUser = createUser;
        this.getUserProfile = getUserProfile;
        this.updateUser = updateUser;
        this.deleteUser = deleteUser;
    }

    private String getLoggedUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserEntity) {
            return ((UserEntity) principal).getEmail();
        } else if (principal instanceof String) {
            return (String) principal;
        }

        throw new RuntimeException("User not found");
    }

//    @PostMapping("/create")
//    public void create(@RequestBody UserRequestDTO dto) {
//        String encodedPass = new BCryptPasswordEncoder().encode(dto.password());
//        createUser.execute(dto.cpf(), dto.name(), dto.email(), encodedPass);
//    }

    @GetMapping("/me")
    public UserResponseDTO getMe() {
        String email = getLoggedUserEmail();
        User user = getUserProfile.execute(email);
        return new UserResponseDTO(user.getName(), user.getEmail());
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateMe(@RequestBody UserUpdateDTO dto) {
        String email = getLoggedUserEmail();

        updateUser.execute(email, dto.name(), dto.currPassword(), dto.newPassword());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public void deleteMe() {
        String email = getLoggedUserEmail();
        deleteUser.execute(email);
    }
}