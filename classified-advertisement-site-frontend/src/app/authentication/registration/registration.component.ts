import { Component, EventEmitter, Output } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthenticationService } from 'src/app/openapi/userservice';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent {
  @Output() registered: EventEmitter<string> = new EventEmitter<string>();
  email: string = '';
  username: string = '';
  password: string = '';
  confirmPassword: string = '';

  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly snackBar: MatSnackBar,
  ) { }

  onRegister() {
    this.authenticationService.postAuthRegister({ username: this.username, email: this.email, password: this.password, confirmPassword: this.confirmPassword }).subscribe({
      next: () => {
        this.registered.emit(this.username);
        this.username = '';
        this.email = '';
        this.password = '';
        this.confirmPassword = '';
        this.snackBar.open('Successful registration', 'OK', { duration: 5000 });
      },
      error: err => this.snackBar.open(err.error, 'OK', { duration: 5000 }),
    });
  }
}
