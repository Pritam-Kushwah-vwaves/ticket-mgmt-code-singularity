package com.ticket.ticket_managmenet.Controller;

import com.ticket.ticket_managmenet.Model.Ticket_Entity;
import com.ticket.ticket_managmenet.Model.User;
import com.ticket.ticket_managmenet.Repository.UserRepository;
import com.ticket.ticket_managmenet.Service.Ticket_Service;
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

    @PostMapping(
            value = "/create",
            consumes = "multipart/form-data"
    )
    public Ticket_Entity createTicket(
            @RequestPart("file") MultipartFile file,
            @RequestHeader(value = "X-Username", required = false) String username
    ) {
        System.out.println("Aa gai request");
        User currentUser = getCurrentUser(username);
        return ticketService.createTicket(file, currentUser);
    }

    /* GET TICKETS */
    /* ADMIN → ALL | GENERAL → OWN */

    @GetMapping
    public List<Ticket_Entity> getTickets(
            @RequestHeader(value = "X-Username", required = false) String username
    ) {
        User currentUser = getCurrentUser(username);
        return ticketService.getTicketsForUser(currentUser);
    }

    @GetMapping("getTickets")
    public List<Ticket_Entity> getAllTickets()
    {
        return ticketService.getAllTicketsForAdmin();
    }

    /* GET BY ID */
    /* ADMIN or OWNER */

    @GetMapping("/{id}")
    public Ticket_Entity getTicketById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Username", required = false) String username
    ) {
        // Username can be used for authorization checks if needed in the future
        return ticketService.getTicketById(id);
    }

    /* UPDATE */
    /* ADMIN or OWNER */

    @PutMapping("/update/{id}")
    public Ticket_Entity updateTicket(
            @PathVariable Long id,
            @RequestBody Ticket_Entity ticket,
            @RequestHeader(value = "X-Username", required = false) String username
    ) {
        User currentUser = getCurrentUser(username);
        return ticketService.updateTicket(id, ticket, currentUser);
    }

    /* DELETE */
    /* ADMIN or OWNER */

    @DeleteMapping("/delete/{id}")
    public String deleteTicket(
            @PathVariable Long id,
            @RequestHeader(value = "X-Username", required = false) String username
    ) {
        User currentUser = getCurrentUser(username);
        ticketService.deleteTicket(id, currentUser);
        return "Ticket deleted successfully";
    }

    /* ASSIGN TICKET */
    /* ADMIN ONLY */

    @PutMapping("/{ticketId}/assign/{userId}")
    public Ticket_Entity assignTicket(
            @PathVariable Long ticketId,
            @PathVariable Long userId,
            @RequestHeader(value = "X-Username", required = false) String username
    ) {
        User currentUser = getCurrentUser(username);
        return ticketService.assignTicket(ticketId, userId, currentUser);
    }

    /* HELPER */

    private User getCurrentUser(String username) {
        if (username == null || username.isEmpty()) {
            // If no username provided, get first user as default (or handle as needed)
            return userRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No users found in database"));
        }
        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + username));
    }
}