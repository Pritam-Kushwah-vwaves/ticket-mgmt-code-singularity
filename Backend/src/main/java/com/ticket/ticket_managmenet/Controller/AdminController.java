package com.ticket.ticket_managmenet.Controller;

import com.ticket.ticket_managmenet.Model.Ticket_Entity;
import com.ticket.ticket_managmenet.Model.User;
import com.ticket.ticket_managmenet.Repository.UserRepository;
import com.ticket.ticket_managmenet.Service.Ticket_Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/tickets")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController
{

    private final Ticket_Service ticketService;
    private final UserRepository userRepository;

    public AdminController(
            Ticket_Service ticketService,
            UserRepository userRepository
    ) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Ticket_Entity> getAllTickets() {
        return ticketService.getAllTicketsForAdmin();
    }

    @PutMapping("/{ticketId}/assign/{userId}")
    public Ticket_Entity assignTicket(
            @PathVariable Long ticketId,
            @PathVariable Long userId,
            @RequestHeader(value = "X-Username", required = false) String username
    ) {
        User currentUser = userRepository
                .findByUsername(username != null ? username : "admin")
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Service will load the assignee by userId
        return ticketService.assignTicket(ticketId, userId, currentUser);
    }
}
