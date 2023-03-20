import { Component } from '@angular/core';
import { AuthService } from 'src/app/openapi/gateway';
import { AuthenticationService } from 'src/app/openapi/userservice';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(
    private readonly authService: AuthService,
    private readonly authenticationService: AuthenticationService,
  ) { }

  onLogin() {
    this.authService.postAuthLogin({ username: this.username, password: this.password }).subscribe({
      next: res => console.log(res),
      error: err => console.error('Fail:', err),
    });
  }

  onResetPassword() {
    this.authenticationService.postAuthResetPassword();
  }
}
