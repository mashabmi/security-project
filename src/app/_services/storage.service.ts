import { Injectable } from "@angular/core";

const USER_KEY = 'auth-user';
const TOKEN_KEY = 'auth-token';

@Injectable({
    providedIn: 'root'
})

export class StorageService {
    
    constructor() {}

    saveToken(token: string): void {
        window.sessionStorage.removeItem(TOKEN_KEY);
        window.sessionStorage.setItem(TOKEN_KEY, token);
    }

    getToken(): string | null {
        return sessionStorage.getItem(TOKEN_KEY);
    }

    removeToken(): void {
        window.sessionStorage.removeItem(TOKEN_KEY);
    }

    saveUser(user: any): void {
        window.sessionStorage.removeItem(USER_KEY);
        window.sessionStorage.setItem(USER_KEY, JSON.stringify(user));
    }
    
    getUser(): any {
        const user = window.sessionStorage.getItem(USER_KEY);
        return user ? JSON.parse(user) : null;
    }

    removeUser(): void {
        window.sessionStorage.removeItem(USER_KEY);
    }
    
    public isLoggedIn(): boolean {
        return !!this.getToken();
    }

    clean(): void {
        window.sessionStorage.clear();
    }

}