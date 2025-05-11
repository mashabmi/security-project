import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, Observable, tap, throwError } from "rxjs";
import { ClientType } from "../_enums/client-type.enum";
import { ServicesPackage } from "../_enums/services-package.enum";
import { UserRole } from "../_enums/user-role.enum";
import { StorageService } from "./storage.service";
import { environment } from "../../environments/environment";

@Injectable({
    providedIn: 'root',
})
export class AuthService {
    private readonly authApi = `${environment.apiUrl}/auth/`;
    private readonly httpOptions = {
        headers: new HttpHeaders({ 
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        })
    };

    constructor(
        private http: HttpClient, 
        private storageService: StorageService
    ) {}

    login(username: string, password: string, role: UserRole, captchaResponse?: string): Observable<any> {
        const payload: any = {
            username,
            password,
            ...(role === UserRole.MODERATOR && {
                role,
                captchaResponse
            })
        };

        return this.http.post(`${this.authApi}signin`, payload).pipe(
            tap((response: any) => {
                this.storageService.saveToken(response.accessToken);
                this.storageService.saveUser(response.user);
            }),
            catchError((error: HttpErrorResponse) => {
                let errorMessage = 'Login failed';
                
                if (error.status === 401) {
                    errorMessage = error.error?.message || 'Invalid credentials. Please try again.';
                } else if (error.status === 403) {
                    errorMessage = error.error?.message || 'You do not have permission to login as this role';
                } else if (error.status === 0) {
                    errorMessage = 'Server unavailable - please try again later';
                }
                
                return throwError(() => ({
                    status: error.status,
                    message: errorMessage,
                    error: error.error
                }));
            })
        );
    }

    register(name: string, lastName: string, username: string, email: string, password: string, confirmPassword: string, address: string, city: string, country: string, phoneNumber: string, 
            companyName: string | null, pib: string | null, clientType: ClientType, servicesPackage: ServicesPackage): Observable<any> {
        const payload = {
            name,
            lastName,
            username,
            email,
            password,
            confirmPassword,
            address,
            city,
            country,
            phoneNumber,
            companyName,
            pib,
            clientType,
            servicesPackage
        }

        return this.http.post(`${this.authApi}signup`, payload, this.httpOptions).pipe(
            tap((response: any) => {
                if (response.accessToken) {
                    this.storageService.saveToken(response.accessToken);
                    this.storageService.saveUser(response.user);
                }
            }),
            catchError((error: HttpErrorResponse) => {
                let errorMessage = 'Registration failed';
                
                if (error.error?.message) {
                    errorMessage = error.error.message;
                } else if (error.status === 0) {
                    errorMessage = 'Server unavailable - please try again later';
                } else if (error.status === 401) {
                    errorMessage = 'Registration endpoint misconfigured (should not require auth)';
                } else if (error.status === 400) {
                    errorMessage = 'Invalid registration data';
                }
            
                
                return throwError(() => ({
                    status: error.status,
                    message: errorMessage,
                    error: error.error
                }));
            })
        );
    }

    logout(): Observable<any> {
        return this.http.post(`${this.authApi}signout`, {}, this.httpOptions).pipe(
          tap(() => {
            this.storageService.clean();
          })
        );
    }

    isLoggedIn(): boolean {
        return this.storageService.isLoggedIn();
    }
}