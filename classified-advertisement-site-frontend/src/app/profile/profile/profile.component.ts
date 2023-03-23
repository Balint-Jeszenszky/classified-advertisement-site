import { Component, OnInit } from '@angular/core';
import { UserProfileService } from 'src/app/openapi/userservice';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  username: string = '';
  email: string = '';
  roles: string[] = [];

  constructor(private readonly userProfileService: UserProfileService) { }

  ngOnInit(): void {
    this.userProfileService.getProfile().subscribe(res => {
      this.username = res.username;
      this.email = res.email;
      this.roles = res.roles;
    });
  }

  get rolesString() {
    return this.roles.map(r => r.replace('ROLE_', '')).join(', ');
  }
}
