import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { TicketService } from '../../services/ticket.service';
import { AuthService } from '../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ticket-add',
  standalone:true,
  imports:[FormsModule, RouterModule, CommonModule],
  templateUrl: './ticket-add.component.html',
  styleUrls: ['./ticket-add.component.css']
})
export class TicketAddComponent {

  ticket = {
    title: '',
    description: ''
  };

  selectedFile: File | null = null;
  fileName: string = '';
  fileSize: string = '';
  isUploading: boolean = false;
  username: string | null = '';

  constructor(
    private ticketService: TicketService,
    private authService: AuthService,
    private router: Router
  ) {
    this.username = this.authService.getCurrentUsername();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.fileName = file.name;
      this.fileSize = this.formatFileSize(file.size);
    }
  }

  removeFile() {
    this.selectedFile = null;
    this.fileName = '';
    this.fileSize = '';
    // Reset file input
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  }

  submit() {
    if (!this.selectedFile) {
      alert('Please select a PDF file to upload');
      return;
    }

    // Check if user is logged in
    if (!this.authService.isLoggedIn()) {
      alert('You must be logged in to create a ticket. Redirecting to login...');
      this.router.navigate(['/login']);
      return;
    }

    // Verify auth header is available
    if (!this.authService.getAuthHeader()) {
      alert('Authentication error. Please login again.');
      this.router.navigate(['/login']);
      return;
    }

    this.isUploading = true;

    this.ticketService.createTicket(this.selectedFile).subscribe({
      next: () => {
        this.isUploading = false;
        alert('Ticket created successfully! The document has been processed and summarized.');
        this.ticket.title = '';
        this.ticket.description = '';
        this.removeFile();
        this.router.navigate(['/tickets/list']);
      },
      error: (error) => {
        this.isUploading = false;
        console.error('Error creating ticket:', error);
        if (error.status === 400) {
          alert('Error: Please ensure the file is a valid PDF and try again.');
        } else if (error.status === 401) {
          alert('Authentication failed. Please login again.');
          this.router.navigate(['/login']);
        } else {
          alert('Error creating ticket. Please try again.');
        }
      }
    });
  }
}
