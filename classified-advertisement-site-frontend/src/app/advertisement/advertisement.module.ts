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
import { MatIconModule } from '@angular/material/icon';
import { MatStepperModule } from '@angular/material/stepper';
import { SharedModule } from '../shared/shared.module';
import { AdvertisementListComponent } from './advertisement-list/advertisement-list.component';
import { AdvertisementListItemComponent } from './advertisement-list/advertisement-list-item/advertisement-list-item.component';
import { SearchComponent } from './components/search/search.component';
import { HomeComponent } from './home/home.component';
import { AdvertisementCardComponent } from './home/advertisement-card/advertisement-card.component';
import { ManageAdvertisementComponent } from './manage-advertisement/manage-advertisement.component';
import { EditAdvertisementComponent } from './manage-advertisement/edit-advertisement/edit-advertisement.component';
import { ImageUploadComponent } from './manage-advertisement/image-upload/image-upload.component';
import { loginGuard } from '../auth.guard';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { CommercialPriceComponent } from './details/commercial-price/commercial-price.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { NgxMatDateFormats, NgxMatDatetimePickerModule, NgxMatTimepickerModule, NGX_MAT_DATE_FORMATS } from '@angular-material-components/datetime-picker';
import { NgxMatMomentModule } from '@angular-material-components/moment-adapter';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'advertisement/new', component: ManageAdvertisementComponent, data: { login: true }, canActivate: [loginGuard], },
  { path: 'advertisement/edit/:id', component: ManageAdvertisementComponent, data: { login: true}, canActivate: [loginGuard], },
  { path: 'advertisement/:id', component: DetailsComponent },
  { path: 'advertisement/:id/:title', component: DetailsComponent },
  { path: 'category/:id', component: AdvertisementListComponent },
  { path: 'category/:id/:query', component: AdvertisementListComponent },
  { path: 'search/:query', component: AdvertisementListComponent },
];

const defaultTimeFormat = 'YYYY. MM. DD. HH:mm';
const defaultMonthYearLabel = 'YYYY. MM.';
const CUSTOM_DATE_FORMATS: NgxMatDateFormats = {
  parse: {
    dateInput: defaultTimeFormat,
  },
  display: {
    dateInput: defaultTimeFormat,
    monthYearLabel: defaultMonthYearLabel,
    dateA11yLabel: defaultTimeFormat,
    monthYearA11yLabel: defaultMonthYearLabel,
  },
};

@NgModule({
  declarations: [
    DetailsComponent,
    GalleryComponent,
    CommentSectionComponent,
    NewCommentComponent,
    CommentComponent,
    AdvertisementListComponent,
    AdvertisementListItemComponent,
    SearchComponent,
    HomeComponent,
    AdvertisementCardComponent,
    ManageAdvertisementComponent,
    EditAdvertisementComponent,
    ImageUploadComponent,
    CommercialPriceComponent
  ],
  imports: [
    RouterModule.forChild(routes),
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatStepperModule,
    MatSelectModule,
    MatChipsModule,
    SharedModule,
    MatDatepickerModule,
    NgxMatDatetimePickerModule,
    NgxMatTimepickerModule,
    NgxMatMomentModule,
  ],
  providers: [
    {
      provide: NGX_MAT_DATE_FORMATS,
      useValue: CUSTOM_DATE_FORMATS,
    },
  ],
})
export class AdvertisementModule { }
