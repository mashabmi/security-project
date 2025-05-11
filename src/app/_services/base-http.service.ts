import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export abstract class BaseHttpService {
  protected readonly apiUrl = environment.apiUrl;

  constructor(protected http: HttpClient) {}
}