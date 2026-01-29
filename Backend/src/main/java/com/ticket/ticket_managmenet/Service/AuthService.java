package com.ticket.ticket_managmenet.Service;

import com.ticket.ticket_managmenet.Enumiration.Role;
import com.ticket.ticket_managmenet.Model.User;
import com.ticket.ticket_managmenet.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRole(Role.GENERAL);

        return userRepository.save(user);
    }

    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return passwordEncoder.matches(password, user.getPassword());
    }
}
