import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { UserProfileService } from 'src/app/openapi/userservice';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss']
})
export class EditProfileComponent implements OnInit {
  email: string = '';
  oldPassword: string = '';
  newPassword: string = '';
  confirmNewPassword: string = '';

  constructor(
    private readonly userProfileService: UserProfileService,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.userProfileService.getProfile().subscribe(res => {
      this.email = res.email;
    });
  }

  onSubmit() {
    this.userProfileService.putProfile({
      email: this.email,
      oldPassword: this.oldPassword,
      newPassword: this.newPassword,
      confirmNewPassword: this.confirmNewPassword
    }).subscribe({
      next: () => this.router.navigate(['/profile']),
      error: err => this.snackBar.open(err.error, 'OK', { duration: 5000 }),
    });
  }
}
