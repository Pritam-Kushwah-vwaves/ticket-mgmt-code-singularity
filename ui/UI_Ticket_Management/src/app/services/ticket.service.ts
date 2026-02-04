import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Ticket } from '../models/ticket';

@Injectable({
  providedIn: 'root'
})
export class TicketService {

  private baseUrl = 'http://localhost:8082/tickets';

  constructor(private http: HttpClient) {}

  // Health check endpoint
  check(): Observable<string> {
    return this.http.get<string>(`${this.baseUrl}/check`, { responseType: 'text' as 'json' });
  }

  // Get all tickets (role-based: ADMIN gets all, GENERAL gets own)
  getAllTickets(): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.baseUrl}`);
  }

  // Create a new ticket with file upload (backend extracts title/description from PDF)
  createTicket(file: File): Observable<Ticket> {
    if (!file) {
      throw new Error('File is required to create a ticket');
    }
    
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<Ticket>(`${this.baseUrl}/create`, formData);
  }

  // Get ticket by ID
  getTicketById(id: number): Observable<Ticket> {
    return this.http.get<Ticket>(`${this.baseUrl}/${id}`);
  }

  // Update ticket
  updateTicket(id: number, ticket: Ticket): Observable<Ticket> {
    return this.http.put<Ticket>(`${this.baseUrl}/update/${id}`, ticket);
  }

  // Delete ticket
  deleteTicket(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/delete/${id}`, { 
      responseType: 'text' as 'json'
    }) as Observable<string>;
  }

  // Assign ticket to user (ADMIN only) - uses admin endpoint
  assignTicket(ticketId: number, userId: number): Observable<Ticket> {
    return this.http.put<Ticket>(`http://localhost:8082/admin/tickets/${ticketId}/assign/${userId}`, {});
  }
}
