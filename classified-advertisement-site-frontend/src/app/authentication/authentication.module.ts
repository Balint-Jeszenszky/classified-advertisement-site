import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthenticationComponent } from './authentication.component';
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ConfirmEmailComponent } from './confirm-email/confirm-email.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { MatDialogModule } from '@angular/material/dialog';
import { ResetPasswordDialogComponent } from './reset-password-dialog/reset-password-dialog.component';
import { SharedModule } from '../shared/shared.module';

const routes: Routes = [
  { path: '', component: AuthenticationComponent },
  { path: 'confirmEmail/:key', component: ConfirmEmailComponent },
  { path: 'resetPassword/:key', component: ResetPasswordComponent },
];

@NgModule({
  declarations: [
    AuthenticationComponent,
    LoginComponent,
    RegistrationComponent,
    ConfirmEmailComponent,
    ResetPasswordComponent,
    ResetPasswordDialogComponent,
  ],
  imports: [
    RouterModule.forChild(routes),
    CommonModule,
    SharedModule,
    FormsModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatDialogModule,
  ]
})
export class AuthenticationModule { }
