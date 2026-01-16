package com.ticket.ticket_managmenet.Controller;

import com.ticket.ticket_managmenet.Model.Ticket_Entity;
import com.ticket.ticket_managmenet.Model.User;
import com.ticket.ticket_managmenet.Repository.UserRepository;
import com.ticket.ticket_managmenet.Service.Ticket_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/tickets")
@PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasAuthority('TICKET_VIEW_ALL')")
    @GetMapping
    public List<Ticket_Entity> getAllTickets() {
        return ticketService.getAllTicketsForAdmin();
    }

    @PreAuthorize("hasAuthority('TICKET_ASSIGN')")
    @PutMapping("/{ticketId}/assign/{userId}")
    public Ticket_Entity assignTicket(
            @PathVariable Long ticketId,
            @PathVariable Long userId,
            Authentication authentication   // ✅ who is doing the assignment
    ) {
        User currentUser = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        // Service will load the assignee by userId
        return ticketService.assignTicket(ticketId, userId, currentUser);
    }
}
