import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { StorageService } from "./storage.service";
import { environment } from "../../environments/environment";

@Injectable({
    providedIn: 'root'
})
export class AdminService {
    private readonly apiUrl = environment.apiUrl;

    constructor(
        private http: HttpClient, 
        private storageService: StorageService
    ) {}

    private getAuthHeaders() {
        const token = this.storageService.getToken();
        return new HttpHeaders({
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        });
    }

    getPublicContent(): Observable<string> {
        return this.http.get(`${this.apiUrl}all`, { 
            headers: this.getAuthHeaders(),
            responseType: 'text'
        });
    }

    getAdminBoard(): Observable<string> {
        return this.http.get(`${this.apiUrl}admin`, { 
            headers: this.getAuthHeaders(),
            responseType: 'text'
        });
    }
}