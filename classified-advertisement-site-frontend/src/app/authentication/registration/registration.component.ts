import { Component } from '@angular/core';
import { AuthenticationService } from 'src/app/openapi/userservice';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent {
  email: string = '';
  username: string = '';
  password: string = '';
  confirmPassword: string = '';

  constructor(private readonly authenticationService: AuthenticationService) { }

  onRegister() {
    this.authenticationService.postAuthRegister({ username: this.username, email: this.email, password: this.password, confirmPassword: this.confirmPassword }).subscribe({
      next: res => console.log('Success:', res),
      error: err => console.error('Fail:', err),
    });
  }
}
