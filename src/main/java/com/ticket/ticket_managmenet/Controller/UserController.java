package com.ticket.ticket_managmenet.Controller;

import com.ticket.ticket_managmenet.Dto.UserResponseDTO;
import com.ticket.ticket_managmenet.Model.User;
import com.ticket.ticket_managmenet.Repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAuthority('USER_VIEW')")
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @PreAuthorize("hasAuthority('USER_VIEW') or hasAuthority('TICKET_CREATE')")
    @GetMapping("/me")
    public UserResponseDTO getCurrentUser(Authentication authentication) {
        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getRole().getPermissions()
                        .stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet())
        );
    }
}

