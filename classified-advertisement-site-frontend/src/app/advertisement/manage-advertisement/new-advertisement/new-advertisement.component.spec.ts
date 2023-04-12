import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewAdvertisementComponent } from './new-advertisement.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('NewAdvertisementComponent', () => {
  let component: NewAdvertisementComponent;
  let fixture: ComponentFixture<NewAdvertisementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewAdvertisementComponent ],
      imports: [
        HttpClientTestingModule,
        MatFormFieldModule,
        MatSelectModule,
        FormsModule,
        MatInputModule,
        BrowserAnimationsModule,
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewAdvertisementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
