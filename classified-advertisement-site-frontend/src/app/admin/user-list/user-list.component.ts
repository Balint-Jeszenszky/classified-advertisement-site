import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ModifyUserRequest, UserDetailsResponse, UserManagementService } from 'src/app/openapi/userservice';
import { EditUserDialogComponent } from './edit-user-dialog/edit-user-dialog.component';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {
  displayedColumns: string[] = ['username', 'email', 'roles', 'enabled', 'edit'];
  users: UserDetailsResponse[] = [];

  constructor(
    private readonly userManagementService: UserManagementService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  editUser(user: UserDetailsResponse) {
    const dialogRef = this.dialog.open(EditUserDialogComponent, {
      width: '300px',
      data: user,
    });

    dialogRef.afterClosed().subscribe((editedUser: ModifyUserRequest) => {
      if (editedUser) {
        this.userManagementService.putUsersUserId(user.id, editedUser).subscribe({
          next: () => {
            this.snackBar.open('User updated', 'OK', { duration: 5000 });
            this.loadUsers();
          },
          error: err => this.snackBar.open(err.error, 'OK', { duration: 5000 }),
        });
      }
    });
  }

  private loadUsers() {
    this.userManagementService.getUsersAll().subscribe(res => {
      this.users = res;
    });
  }
}
