import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageAdvertisementComponent } from './manage-advertisement.component';
import { RouterTestingModule } from '@angular/router/testing';
import { MatStepperModule } from '@angular/material/stepper';
import { ImageUploadComponent } from './image-upload/image-upload.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { EditAdvertisementComponent } from './edit-advertisement/edit-advertisement.component';
import { MatIconModule } from '@angular/material/icon';

describe('ManageAdvertisementComponent', () => {
  let component: ManageAdvertisementComponent;
  let fixture: ComponentFixture<ManageAdvertisementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManageAdvertisementComponent, EditAdvertisementComponent, ImageUploadComponent ],
      imports: [
        RouterTestingModule,
        MatStepperModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        MatFormFieldModule,
        MatSelectModule,
        FormsModule,
        MatInputModule,
        MatIconModule,
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageAdvertisementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
