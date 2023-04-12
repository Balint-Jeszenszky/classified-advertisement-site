import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageAdvertisementComponent } from './manage-advertisement.component';
import { RouterTestingModule } from '@angular/router/testing';
import { MatStepperModule } from '@angular/material/stepper';
import { ImageUploadComponent } from './image-upload/image-upload.component';
import { NewAdvertisementComponent } from './new-advertisement/new-advertisement.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('ManageAdvertisementComponent', () => {
  let component: ManageAdvertisementComponent;
  let fixture: ComponentFixture<ManageAdvertisementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManageAdvertisementComponent, ImageUploadComponent, NewAdvertisementComponent ],
      imports: [
        RouterTestingModule,
        MatStepperModule,
        BrowserAnimationsModule,
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
