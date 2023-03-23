import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from 'src/app/openapi/userservice';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  password: string = '';
  confirmPassword: string = '';
  key: string = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly authenticationService: AuthenticationService,
    private readonly snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.key = params['key'];
    });
  }

  onSubmit() {
    this.authenticationService.putAuthResetPassword({ password: this.password, confirmPassword: this.confirmPassword, key: this.key }).subscribe({
      next: () => {
        this.snackBar.open('Password reset successful', 'OK', { duration: 5000 });
        this.router.navigate(['/auth']);
      },
      error: err => this.snackBar.open(err.error, 'OK', { duration: 5000 }),
    });
  }
}
