package com.ticket.ticket_managmenet.Controller;

import com.ticket.ticket_managmenet.Model.Ticket_Entity;
import com.ticket.ticket_managmenet.Model.User;
import com.ticket.ticket_managmenet.Repository.UserRepository;
import com.ticket.ticket_managmenet.Service.Ticket_Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@CrossOrigin(origins = "http://localhost:4200")
public class Ticket_Controller {

    private final Ticket_Service ticketService;
    private final UserRepository userRepository;

    public Ticket_Controller(
            Ticket_Service ticketService,
            UserRepository userRepository
    ) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }

    @GetMapping("/check")
    public String check() {
        return "OK";
    }

    @PreAuthorize("hasAuthority('TICKET_CREATE')")
    @PostMapping(
            value = "/create",
            consumes = "multipart/form-data"
    )
    public Ticket_Entity createTicket(
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) {
        System.out.println("Aa gai request");
        User currentUser = getCurrentUser(authentication);
        return ticketService.createTicket(file, currentUser);
    }

    /* GET TICKETS */
    /* ADMIN → ALL | GENERAL → OWN */

    @GetMapping
    public List<Ticket_Entity> getTickets(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        return ticketService.getTicketsForUser(currentUser);
    }

    /* GET BY ID */
    /* ADMIN or OWNER */

    @GetMapping("/{id}")
    public Ticket_Entity getTicketById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        return ticketService.getTicketById(id);
    }

    /* UPDATE */
    /* ADMIN or OWNER */

    @PutMapping("/update/{id}")
    public Ticket_Entity updateTicket(
            @PathVariable Long id,
            @RequestBody Ticket_Entity ticket,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        return ticketService.updateTicket(id, ticket, currentUser);
    }

    /* DELETE */
    /* ADMIN or OWNER */

    @DeleteMapping("/delete/{id}")
    public String deleteTicket(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        ticketService.deleteTicket(id, currentUser);
        return "Ticket deleted successfully";
    }

    /* ASSIGN TICKET */
    /* ADMIN ONLY */

    @PutMapping("/{ticketId}/assign/{userId}")
    public Ticket_Entity assignTicket(
            @PathVariable Long ticketId,
            @PathVariable Long userId,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        return ticketService.assignTicket(ticketId, userId, currentUser);
    }

    /* HELPER */

    private User getCurrentUser(Authentication authentication) {
        return userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
}