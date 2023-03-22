import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginResponse } from '../openapi/gateway';

@Injectable({
  providedIn: 'root'
})
export class LoggedInUserServiceService {

  constructor() { }

  login(username: string, password: string): Observable<LoginResponse> {
    return new Observable<LoginResponse>();
  }
}
