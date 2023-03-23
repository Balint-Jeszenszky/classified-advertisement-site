import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserDetailsResponse } from 'src/app/openapi/userservice';
import { Role } from 'src/app/service/types';

@Component({
  selector: 'app-edit-user-dialog',
  templateUrl: './edit-user-dialog.component.html',
  styleUrls: ['./edit-user-dialog.component.scss']
})
export class EditUserDialogComponent implements OnInit {
  email: string = '';
  roles?: Role[];
  enabled: boolean = false;
  allRoles = Object.keys(Role).filter((item) => isNaN(Number(item)));

  constructor(
    private readonly dialogRef: MatDialogRef<EditUserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: UserDetailsResponse,
  ) { }

  ngOnInit(): void {
    this.email = this.data.email;
    this.roles = this.data.roles as Role[];
    this.enabled = this.data.enabled;
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  hasRole(role: string) {
    return this.roles?.includes(role as Role);
  }

  toggleRole(role: string) {
    this.roles?.includes(role as Role)
    ? this.roles = this.roles.filter(r => r !== role)
    : this.roles?.push(role as Role);
  }
}
