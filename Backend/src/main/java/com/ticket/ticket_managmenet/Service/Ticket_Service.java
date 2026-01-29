package com.ticket.ticket_managmenet.Service;

import com.ticket.ticket_managmenet.Enumiration.Role;
import com.ticket.ticket_managmenet.Model.Ticket_Entity;
import com.ticket.ticket_managmenet.Model.User;
import com.ticket.ticket_managmenet.Repository.TicketRepository;
import com.ticket.ticket_managmenet.Repository.UserRepository;
import com.ticket.ticket_managmenet.Utility.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class Ticket_Service {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final LLM_Service llmService;

    public Ticket_Service(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            LLM_Service llmService) {

        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.llmService = llmService;
    }

    /* ===================== CREATE TICKET ===================== */

    public Ticket_Entity createTicket(MultipartFile file, User currentUser) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("PDF file is required to create a ticket");
        }

        try {
            String documentText = FileUtil.extractText(file);

            if (documentText == null || documentText.isBlank()) {
                throw new RuntimeException("Uploaded PDF has no readable content");
            }

            String title = extractTitle(documentText);
            String description = extractDescription(documentText);

            String summary;
            try {
                summary = llmService.summarizeText(documentText);
            } catch (Exception e) {
                summary = "AI summary not available";
            }

            Ticket_Entity ticket = new Ticket_Entity();
            ticket.setTitle(title);
            ticket.setDescription(description);
            ticket.setDocumentName(file.getOriginalFilename());
            ticket.setDocumentSummary(summary);
            ticket.setCreatedBy(currentUser);

            return ticketRepository.save(ticket);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create ticket from PDF", e);
        }
    }

    /* ===================== ROLE BASED FETCH ===================== */

    public List<Ticket_Entity> getTicketsForUser(User user) {

        if (user.getRole() == Role.ADMIN) {
            // Admin sees all tickets
            return ticketRepository.findAll();
        }

        // GENERAL user: tickets they created OR are assigned to
        return ticketRepository.findByCreatedByOrAssignedTo(user, user);
    }

    public List<Ticket_Entity>getAllTicketsForAdmin()
    {
        return  ticketRepository.findAll();
    }

    /* ===================== ASSIGN TICKET (ADMIN ONLY) ===================== */

    public Ticket_Entity assignTicket(Long ticketId, Long userId, User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only ADMIN can assign tickets");
        }

        Ticket_Entity ticket = getTicketById(ticketId);

        User assignee = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ticket.setAssignedTo(assignee);
        return ticketRepository.save(ticket);
    }

    /* ===================== UPDATE TICKET ===================== */

    public Ticket_Entity updateTicket(Long id, Ticket_Entity ticket, User currentUser) {

        Ticket_Entity existing = getTicketById(id);

        if (currentUser.getRole() != Role.ADMIN &&
                !existing.getCreatedBy().getId().equals(currentUser.getId())) {

            throw new RuntimeException("You are not allowed to update this ticket");
        }

        if (ticket.getTitle() != null) {
            existing.setTitle(ticket.getTitle());
        }

        if (ticket.getDescription() != null) {
            existing.setDescription(ticket.getDescription());
        }

        return ticketRepository.save(existing);
    }

    /* ===================== DELETE TICKET ===================== */

    public void deleteTicket(Long id, User currentUser) {

        Ticket_Entity ticket = getTicketById(id);

        if (currentUser.getRole() != Role.ADMIN &&
                !ticket.getCreatedBy().getId().equals(currentUser.getId())) {

            throw new RuntimeException("You are not allowed to delete this ticket");
        }

        ticketRepository.deleteById(id);
    }

    /* ===================== COMMON ===================== */

    public Ticket_Entity getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Ticket not found with id " + id));
    }

    /* ===================== PDF HELPERS ===================== */

    private String extractTitle(String text) {
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                return line.trim();
            }
        }
        return "Untitled Ticket";
    }

    private String extractDescription(String text) {
        String[] lines = text.split("\\r?\\n", 2);
        return lines.length > 1
                ? lines[1].trim()
                : "No description available";
    }
}
