import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoggedInUserService } from 'src/app/service/logged-in-user.service';
import { Role } from 'src/app/service/types';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  loggedIn: boolean = false;
  admin: boolean = false;

  constructor(
    private readonly loggedInUserService: LoggedInUserService,
    private readonly router: Router,
  ) { }

  ngOnInit(): void {
    this.loggedInUserService.user.subscribe(u => {
      this.loggedIn = !!u;
      this.admin = !!u?.roles.includes(Role.ROLE_ADMIN);
    });
  }

  logout() {
    this.loggedInUserService.logout();
    this.router.navigate(['/']);
  }
}
