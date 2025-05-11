import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { StorageService } from './_services/storage.service';
import { AuthService } from './_services/auth.service';
import { EventBusService } from './_shared/event-bus.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  roles: string[] = [];
  isLoggedIn = false;
  showAdminBoard = false;
  showModeratorBoard = false;
  username?: string;

  eventBusSub?: Subscription;

  constructor(
    private storageService: StorageService,
    private authService: AuthService,
    private eventBusService: EventBusService,
    private router: Router

  ) {}

  ngOnInit(): void {
    this.updateLoginState();

    this.eventBusSub = this.eventBusService.on('logout', () => {
      this.logout();
    });
  }

  private updateLoginState(): void {
    this.isLoggedIn = this.storageService.isLoggedIn();

    this.roles = [];
    this.showAdminBoard = false;
    this.showModeratorBoard = false;
    this.username = undefined;

    if (this.isLoggedIn) {
      const user = this.storageService.getUser();
      this.roles = user.roles;
      this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
      this.showModeratorBoard = this.roles.includes('ROLE_MODERATOR');
      this.username = user.username;

    }

    this.eventBusSub = this.eventBusService.on('logout', () => {
      this.logout();
    });
  }

  logout(): void {
    const roles = this.roles || [];
    this.authService.logout().subscribe({
      next: res => {
        console.log(res);
        this.storageService.clean();
        this.updateLoginState();
        this.isLoggedIn = false;
        
        let redirectPath = '/login';

        // Navigate without full page reload
        this.router.navigate([redirectPath])
        .then(() => {
          // Optional: Reset any lingering state if needed
        })
        .catch(err => {
          console.error('Navigation error:', err);
          // Fallback to default login if navigation fails
          window.location.href = redirectPath;
        });
      },
      error: err => {
        console.log(err);
        this.storageService.clean();
        this.router.navigate(['/login']);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.eventBusSub) {
      this.eventBusSub.unsubscribe();
    }
  }
}