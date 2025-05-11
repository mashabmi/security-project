import { Component, OnInit } from "@angular/core";
import { UserService } from "../_services/user.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";

@Component({
    selector: 'app-board-admin',
    standalone: true,
    imports: [FormsModule, CommonModule],
    templateUrl: './board-admin.component.html',
    template: `
    <div class="container">
      <header class="jumbotron">
        @if (loading) {
          <p>Loading...</p>
        } @else if (error) {
          <p class="text-danger">{{ error }}</p>
        } @else {
          <p>{{ content }}</p>
        }
      </header>
    </div>
    `
  })
export class BoardAdminComponent implements OnInit {
    content?: string;
    loading = true;
    error?: string;

    constructor(private userService: UserService) {}

    ngOnInit(): void {
        this.userService.getAdminBoard().subscribe({
            next: data => {
                this.content = data;
                this.loading = false;
            },
            error: err => {
                this.loading = false;
                if (err.status === 401 || err.status === 403) {
                  this.error = 'Session expired. Please login again.';
                } else {
                  this.error = err.error?.message || 'An error occurred';
                }
            }
        });
    }
}