import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  username: string;
  roles: string[];
}

interface TokenResponse {
  accessToken: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly apiUrl = environment.apiUrl;
  private readonly httpOptions = {
    withCredentials: true
  };
  private readonly textHttpOptions = {
    ...this.httpOptions,
    responseType: 'text' as const
  };

  constructor(private http: HttpClient) {}

  signIn(credentials: { username: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(
      `${this.apiUrl}/auth/signin`, 
      credentials,
      this.httpOptions
    );
  }

  signUp(userData: { username: string; email: string; password: string }): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/auth/signup`,
      userData,
      this.httpOptions
    );
  }

  refreshToken(): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(
      `${this.apiUrl}/auth/refreshtoken`,
      {},
      this.httpOptions
    );
  }

  signOut(): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/auth/signout`,
      {},
      this.httpOptions
    );
  }

  getPublicContent(): Observable<string> {
    return this.http.get(
      `${this.apiUrl}/test/all`, 
      this.textHttpOptions
    );
  }

  getUserContent(): Observable<string> {
    return this.http.get(
      `${this.apiUrl}/test/user`, 
      this.textHttpOptions
    );
  }

  getModeratorContent(): Observable<string> {
    return this.http.get(
      `${this.apiUrl}/test/mod`, 
      this.textHttpOptions
    );
  }

  getAdminContent(): Observable<string> {
    return this.http.get(
      `${this.apiUrl}/test/admin`, 
      this.textHttpOptions
    );
  }
}