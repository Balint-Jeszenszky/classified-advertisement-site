import { Injectable } from '@angular/core';
import { Observable, ReplaySubject } from 'rxjs';
import { CredentialsService } from '../openapi/credentials.service';
import { AuthService, LoginResponse, RefreshResponse, UserDetailsResponse } from '../openapi/gateway';

const TOKEN_KEY = 'tokens';

type TokenPayload = UserDetailsResponse & {
  exp: number;
}

@Injectable({
  providedIn: 'root'
})
export class LoggedInUserService {
  private tokens?: RefreshResponse;
  private loggedIn: ReplaySubject<boolean> = new ReplaySubject(1);
  private currentUser: ReplaySubject<UserDetailsResponse | undefined> = new ReplaySubject(1);

  constructor(
    private readonly authService: AuthService,
    private readonly credentialsService: CredentialsService,
  ) {
    const tokens = localStorage.getItem(TOKEN_KEY);

    if (!tokens) {
      this.loggedIn.next(false);
        return;
    }

    const parsedTokens = JSON.parse(tokens) as RefreshResponse;

    if (!parsedTokens) {
      this.loggedIn.next(false);
      return;
    }

    if (!this.isExpired(parsedTokens.accessToken)) {
      this.setTokens(parsedTokens);
    } else if (!this.isExpired(parsedTokens.refreshToken)) {
      this.refresh(parsedTokens.refreshToken);
    } else {
      this.logout();
    }
  }

  get isLoggedIn(): Observable<boolean> {
    return this.loggedIn.asObservable();
  }

  get user(): Observable<UserDetailsResponse | undefined> {
    return this.currentUser.asObservable();
  }

  login(username: string, password: string): Observable<LoginResponse> {
    const response = this.authService.postAuthLogin({ username, password });

    return new Observable<LoginResponse>(subscriber => {
      response.subscribe({
        next: res => {
          this.setTokens({ accessToken: res.accessToken, refreshToken: res.refreshToken });
          subscriber.next(res);
        },
        error: err => subscriber.error(err),
      });
    });
  }

  logout(): void {
    const refreshToken = this.tokens?.refreshToken;
    localStorage.removeItem(TOKEN_KEY);
    this.tokens = undefined;
    this.loggedIn.next(false);
    this.currentUser.next(undefined);

    if (!refreshToken) {
      return;
    }

    this.authService.deleteApiAuthLogout({ refreshToken }).subscribe();
  }

  private refresh(refreshToken: string): void {
    this.authService.postApiAuthRefresh({ refreshToken }).subscribe({
      next: res => {
        this.setTokens(res);
      },
      error: err => {
        console.error(err);
        this.logout();
      }
    });
  }

  private isExpired(token: string): boolean {
    const payload = this.getTokenPayload(token);
    const expiration = payload.exp * 1000;
    return new Date(expiration) < new Date();
  }

  private setTokens(tokens: RefreshResponse): void {
    this.credentialsService.updateCredentials(tokens.accessToken);
    this.tokens = tokens;
    this.loggedIn.next(true);
    const { id, username, email, roles, exp } = this.getTokenPayload(tokens.accessToken);
    this.currentUser.next({ id, username, email, roles });
    const expiration = exp * 1000;
    localStorage.setItem(TOKEN_KEY, JSON.stringify(tokens));
    if (tokens.refreshToken) {
      setTimeout(() => this.refresh(tokens.refreshToken), expiration - Date.now() - 10000);
    }
  }

  private getTokenPayload(token: string): TokenPayload {
    const payload = token.split('.')[1];
    const parsed = JSON.parse(window.atob(payload)) as TokenPayload;
    return parsed;
  }
}
