package com.ticket.ticket_managmenet.Controller;

import com.ticket.ticket_managmenet.Model.User;
import com.ticket.ticket_managmenet.Service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/check")
    public String check()
    {
        return "OK";
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        authService.register(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        boolean success = authService.login(user.getUsername(), user.getPassword());
        return success ? "LOGIN_SUCCESS" : "LOGIN_FAILED";
    }
}
