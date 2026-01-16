package com.ticket.ticket_managmenet.Security;

import com.ticket.ticket_managmenet.Repository.TicketRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
public class TicketSecurity {

    private final TicketRepository ticketRepository;

    public TicketSecurity(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public boolean isOwner(Long ticketId, String username) {
        return ticketRepository.findById(ticketId)
                .map(ticket -> ticket.getCreatedBy().getUsername().equals(username))
                .orElse(false);
    }
}

