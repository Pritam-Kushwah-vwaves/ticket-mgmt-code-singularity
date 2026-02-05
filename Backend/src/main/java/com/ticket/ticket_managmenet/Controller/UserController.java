package com.ticket.ticket_managmenet.Controller;

import com.ticket.ticket_managmenet.Dto.UserResponseDTO;
import com.ticket.ticket_managmenet.Model.User;
import com.ticket.ticket_managmenet.Repository.UserRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @GetMapping("/me")
    public UserResponseDTO getCurrentUser(
            @RequestHeader(value = "X-Username", required = false) String username
    ) {
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("X-Username header is required");
        }
        
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

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

