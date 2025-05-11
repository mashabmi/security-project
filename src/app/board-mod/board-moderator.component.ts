import { Component, OnInit } from "@angular/core";
import { UserService } from "../_services/user.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";

@Component({
    selector: 'app-board-moderator',
    standalone: true,
    imports: [FormsModule, CommonModule],
    templateUrl: './board-moderator.component.html',
  })
export class BoardModeratorComponent implements OnInit {
    content?: string;

    constructor(private userService: UserService) {}

    ngOnInit(): void {
        this.userService.getModeratorBoard().subscribe({
            next: data => {
                this.content = data;
            },
            error: err => { console.log(err)
                if(err.error) {
                    this.content = JSON.parse(err.error).message;
                }
                else {
                    this.content = "Error with status" + err.status;
                }
            }
        });
    }
}