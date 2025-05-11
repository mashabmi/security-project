import { Component, OnInit } from "@angular/core";
import { StorageService } from "../_services/storage.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";

@Component ({
    selector: 'app-profile',
    standalone: true,
    imports: [FormsModule, CommonModule],
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
    form: any;

    constructor(private storageService: StorageService) {}

    ngOnInit(): void {
        this.form = this.storageService.getUser();
    }
}