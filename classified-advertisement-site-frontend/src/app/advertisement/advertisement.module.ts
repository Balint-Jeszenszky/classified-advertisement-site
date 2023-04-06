import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { DetailsComponent } from './details/details.component';
import { GalleryComponent } from './details/gallery/gallery.component';
import { CommentSectionComponent } from './details/comment-section/comment-section.component';
import { NewCommentComponent } from './details/comment-section/new-comment/new-comment.component';
import { CommentComponent } from './details/comment-section/comment/comment.component';
import { MatCardModule } from '@angular/material/card';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { SharedModule } from '../shared/shared.module';

const routes: Routes = [
  { path: 'advertisement/:id', component: DetailsComponent },
  { path: 'advertisement/:id/:title', component: DetailsComponent },
];

@NgModule({
  declarations: [
    DetailsComponent,
    GalleryComponent,
    CommentSectionComponent,
    NewCommentComponent,
    CommentComponent
  ],
  imports: [
    RouterModule.forChild(routes),
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    SharedModule,
  ]
})
export class AdvertisementModule { }
