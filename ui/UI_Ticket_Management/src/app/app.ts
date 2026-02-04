import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('UI_Ticket_Management');
  authService: any;

  ngOnInit(){
    this.authService.loadPermissionsFromStorage();
  }
}
