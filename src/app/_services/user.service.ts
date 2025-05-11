import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { StorageService } from "../_services/storage.service";
import { environment } from "../../environments/environment";

const API_URL = `${environment.apiUrl}/test/`;

@Injectable({
    providedIn: 'root'
})
export class UserService {
    
    constructor(private http: HttpClient) {}

    getPublicContent(): Observable<any> {
        return this.http.get(API_URL + 'all', { responseType: 'text' });
    }

    getUserBoard(): Observable<any> {
        return this.http.get(API_URL + 'user', { responseType: 'text' });
    }
    
    getModeratorBoard(): Observable<any> {
        return this.http.get(API_URL + 'mod', { responseType: 'text' });
    }

    getAdminBoard(): Observable<any> {
        return this.http.get(API_URL + 'admin', { responseType: 'text' });
    }
}