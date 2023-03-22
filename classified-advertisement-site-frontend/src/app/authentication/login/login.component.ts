import { Component, Input, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable } from 'rxjs';
import { AuthenticationService } from 'src/app/openapi/userservice';
import { LoggedInUserServiceService } from 'src/app/service/logged-in-user-service.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  @Input() usernameChange?: Observable<string>;
  username: string = '';
  password: string = '';
  showHint: boolean = false;

  constructor(
    private readonly loggedInUserServiceService: LoggedInUserServiceService,
    private readonly authenticationService: AuthenticationService,
    private readonly snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.usernameChange?.subscribe(username => {
      this.username = username;
      this.showHint = true;
    });
  }

  onLogin() {
    this.loggedInUserServiceService.login(this.username, this.password).subscribe({
      error: err => this.snackBar.open(err.error, 'OK', { duration: 5000 }),
    });
  }

  onResetPassword() {
    this.authenticationService.postAuthResetPassword();
  }
}
