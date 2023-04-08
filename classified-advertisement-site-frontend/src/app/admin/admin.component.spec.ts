import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTreeModule } from '@angular/material/tree';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AdminComponent } from './admin.component';
import { CategoriesComponent } from './categories/categories.component';
import { CategoryTreeComponent } from './categories/category-tree/category-tree.component';
import { UserListComponent } from './user-list/user-list.component';

describe('AdminComponent', () => {
  let component: AdminComponent;
  let fixture: ComponentFixture<AdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminComponent, UserListComponent, CategoriesComponent, CategoryTreeComponent ],
      imports: [
        HttpClientTestingModule,
        MatTabsModule,
        MatDialogModule,
        MatSnackBarModule,
        MatTreeModule,
        BrowserAnimationsModule,
        MatTableModule,
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
