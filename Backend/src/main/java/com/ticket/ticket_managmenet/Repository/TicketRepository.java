package com.ticket.ticket_managmenet.Repository;

import com.ticket.ticket_managmenet.Model.Ticket_Entity;
import com.ticket.ticket_managmenet.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket_Entity,Long>
{
    //List<Ticket_Entity> findByCreatedByUsername(String username);

    List<Ticket_Entity> findByCreatedBy(User user);

    List<Ticket_Entity> findByCreatedByOrAssignedTo(User createdBy, User assignedTo);

}
