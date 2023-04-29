import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserListComponent } from './user-list/user-list.component';
import { RouterModule, Routes } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { EditUserDialogComponent } from './user-list/edit-user-dialog/edit-user-dialog.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTreeModule } from '@angular/material/tree';
import { MatSelectModule } from '@angular/material/select';
import { MatMenuModule } from '@angular/material/menu';
import { AdminComponent } from './admin.component';
import { CategoryTreeComponent } from './categories/category-tree/category-tree.component';
import { CategoriesComponent } from './categories/categories.component';
import { EditCategoryComponent } from './categories/edit-category/edit-category.component';
import { WebScraperComponent } from './web-scraper/web-scraper.component';
import { EditSiteDialogComponent } from './web-scraper/edit-site-dialog/edit-site-dialog.component';

const routes: Routes = [
  { path: '', component: AdminComponent },
];

@NgModule({
  declarations: [
    UserListComponent,
    EditUserDialogComponent,
    AdminComponent,
    CategoryTreeComponent,
    CategoriesComponent,
    EditCategoryComponent,
    WebScraperComponent,
    EditSiteDialogComponent
  ],
  imports: [
    RouterModule.forChild(routes),
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSlideToggleModule,
    MatCheckboxModule,
    MatTabsModule,
    MatTreeModule,
    MatSelectModule,
    MatMenuModule,
  ]
})
export class AdminModule { }
