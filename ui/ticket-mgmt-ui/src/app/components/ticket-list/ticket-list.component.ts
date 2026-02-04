import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { TicketService } from '../../services/ticket.service';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { Ticket } from '../../models/ticket';
import { User, Role } from '../../models/user';

@Component({
  selector: 'app-ticket-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './ticket-list.component.html',
  styleUrls: ['./ticket-list.component.css']
})
export class TicketListComponent implements OnInit {

  tickets: Ticket[] = [];
  editingTicketId: number | null = null;
  editedTicket: Ticket = { title: '', description: '' };
  username: string | null = '';
  isAdmin: boolean = false;
  users: User[] = [];
  assigningTicketId: number | null = null;
  selectedUserId: number | null = null;

  constructor(
    private ticketService: TicketService,
    public authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {
    this.username = this.authService.getCurrentUsername();
    // Check role from localStorage
    const storedRole = this.authService.getCurrentUserRole();
    console.log('Stored role in localStorage:', storedRole);
    this.isAdmin = this.authService.isAdmin();
    console.log('Is Admin?', this.isAdmin);
  }

  ngOnInit(): void {
    // First, try to refresh role from backend
    this.refreshRoleIfNeeded();
    this.loadTickets();
  }

  refreshRoleIfNeeded() {
    const currentRole = this.authService.getCurrentUserRole();
    if (!currentRole) {
      // Role not set, try to fetch from backend
      this.authService.refreshUserRole().subscribe({
        next: (role) => {
          if (role) {
            this.isAdmin = this.authService.isAdmin();
            if (this.isAdmin) {
              this.loadUsers();
            }
          }
        },
        error: (error) => {
          console.warn('Could not fetch role, will try to infer from tickets');
        }
      });
    } else {
      this.isAdmin = this.authService.isAdmin();
      if (this.isAdmin) {
        this.loadUsers();
      }
    }
  }

  loadTickets() {
    this.ticketService.getAllTickets().subscribe({
      next: (data) => {
        this.tickets = data;
        // If role still not set, try to infer from ticket data
        if (this.authService.getCurrentUserRole() === null && data.length > 0) {
          const currentUsername = this.authService.getCurrentUsername();
          // Check if user can see tickets from multiple users (likely admin)
          const uniqueCreators = new Set(data.map(t => t.createdBy?.username).filter(Boolean));
          if (uniqueCreators.size > 1) {
            // User can see tickets from multiple users, likely admin
            console.log('Inferring ADMIN role from ticket data');
            localStorage.setItem('userRole', 'ADMIN');
            this.isAdmin = true;
            this.loadUsers();
          } else {
            // User can only see own tickets, likely general
            console.log('Inferring GENERAL role from ticket data');
            localStorage.setItem('userRole', 'GENERAL');
            this.isAdmin = false;
          }
        }
      },
      error: (error) => {
        console.error('Error loading tickets:', error);
        if (error.status === 401) {
          alert('Authentication failed. Please login again.');
          this.authService.logout();
          this.router.navigate(['/login']);
        } else {
          alert('Error loading tickets. Please try again.');
        }
      }
    });
  }

  loadUsers() {
    console.log('Loading users for assignment...');
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        console.log('Users loaded:', users);

        // Filter to only GENERAL users (admins assign to general users)
        // Now roles are 'ADMIN' | 'GENERAL' in both backend and frontend
        this.users = users.filter(u => u.role === Role.GENERAL);

        console.log('Filtered GENERAL users:', this.users);
        if (this.users.length === 0) {
          console.warn('No GENERAL users found for assignment');
        }
      },
      error: (error) => {
        console.error('Error loading users:', error);
        console.error('Error details:', {
          status: error.status,
          message: error.message,
          url: error.url
        });
        if (error.status === 401) {
          alert('Authentication failed. Please login again.');
          this.authService.logout();
          this.router.navigate(['/login']);
        } else if (error.status === 403) {
          alert('You do not have permission to view users.');
        } else if (error.status === 404) {
          alert('Users endpoint not found. Please ensure the backend has /users endpoint.');
        } else {
          alert('Failed to load users for assignment. Please check console for details.');
        }
      }
    });
  }

  startEdit(ticket: Ticket) {
    if (ticket.id) {
      this.editingTicketId = ticket.id;
      this.editedTicket = { ...ticket };
    }
  }

  cancelEdit() {
    this.editingTicketId = null;
    this.editedTicket = { title: '', description: '' };
  }

  updateTicket() {
    if (this.editingTicketId && this.editedTicket.title && this.editedTicket.description) {
      this.ticketService.updateTicket(this.editingTicketId, this.editedTicket).subscribe({
        next: () => {
          alert('Ticket updated successfully');
          this.loadTickets();
          this.cancelEdit();
        },
        error: (error) => {
          console.error('Error updating ticket:', error);
          if (error.status === 401) {
            alert('Authentication failed. Please login again.');
            this.authService.logout();
            this.router.navigate(['/login']);
          } else if (error.status === 403) {
            alert('You are not allowed to update this ticket.');
          } else {
            alert('Error updating ticket. Please try again.');
          }
        }
      });
    }
  }

  deleteTicket(id: number) {
    if (confirm('Are you sure you want to delete this ticket?')) {
      this.ticketService.deleteTicket(id).subscribe({
        next: (response) => {
          console.log('Delete response:', response);
          alert('Ticket deleted successfully');
          this.loadTickets();
        },
        error: (error) => {
          console.error('Error deleting ticket:', error);
          if (error.status === 401) {
            alert('Authentication failed. Please login again.');
            this.authService.logout();
            this.router.navigate(['/login']);
          } else if (error.status === 403) {
            alert('You are not allowed to delete this ticket.');
          } else {
            alert('Error deleting ticket: ' + (error.error?.message || error.message || 'Please try again.'));
          }
        }
      });
    }
  }

  startAssign(ticket: Ticket) {
    if (ticket.id) {
      this.assigningTicketId = ticket.id;
      this.selectedUserId = ticket.assignedTo?.id || null;
    }
  }

  cancelAssign() {
    this.assigningTicketId = null;
    this.selectedUserId = null;
  }

  assignTicket() {
    if (this.assigningTicketId && this.selectedUserId) {
      this.ticketService.assignTicket(this.assigningTicketId, this.selectedUserId).subscribe({
        next: () => {
          alert('Ticket assigned successfully');
          this.loadTickets();
          this.cancelAssign();
        },
        error: (error) => {
          console.error('Error assigning ticket:', error);
          if (error.status === 401) {
            alert('Authentication failed. Please login again.');
            this.authService.logout();
            this.router.navigate(['/login']);
          } else if (error.status === 403) {
            alert('Only ADMIN users can assign tickets.');
          } else {
            alert('Error assigning ticket. Please try again.');
          }
        }
      });
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
