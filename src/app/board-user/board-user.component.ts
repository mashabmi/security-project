import { Component, OnInit } from "@angular/core";
import { UserService } from "../_services/user.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";

@Component({
    selector: 'app-board-user',
    standalone: true,
    imports: [FormsModule, CommonModule],
    templateUrl: './board-user.component.html',
})
export class BoardUserComponent implements OnInit {
    content?: string;

    constructor(private userService: UserService) {}

    ngOnInit(): void {
        this.userService.getUserBoard().subscribe({
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