import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';
import { LoggedInUserService } from 'src/app/service/logged-in-user.service';
import { Role } from 'src/app/service/types';
import { MenuTree } from './submenu/submenu.component';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  loggedIn: boolean = false;
  admin: boolean = false;
  categoryTree?: MenuTree[];

  constructor(
    private readonly loggedInUserService: LoggedInUserService,
    private readonly router: Router,
    private readonly categoryService: CategoryService,
  ) { }

  ngOnInit(): void {
    this.loggedInUserService.user.subscribe(u => {
      this.loggedIn = !!u;
      this.admin = !!u?.roles.includes(Role.ROLE_ADMIN);
    });
    this.categoryService.getCategories().subscribe({
      next: res => this.categoryTree = this.createCategoryTree(res),
    });
  }

  logout() {
    this.loggedInUserService.logout();
    this.router.navigate(['/']);
  }

  private createCategoryTree(categoryList: CategoryResponse[], parentId: number | null = null): MenuTree[] {
    return categoryList.filter(c => c.parentCategoryId === parentId).map(c => ({
      name: c.name,
      id: c.id,
      children: this.createCategoryTree(categoryList, c.id),
    }));
  }
}
