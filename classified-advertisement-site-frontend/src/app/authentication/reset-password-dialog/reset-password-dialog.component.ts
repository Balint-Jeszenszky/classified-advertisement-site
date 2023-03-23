import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-reset-password-dialog',
  templateUrl: './reset-password-dialog.component.html',
  styleUrls: ['./reset-password-dialog.component.scss']
})
export class ResetPasswordDialogComponent {
  email: string = '';

  constructor(
    private readonly dialogRef: MatDialogRef<ResetPasswordDialogComponent>
  ) { }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
