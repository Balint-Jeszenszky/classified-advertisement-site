import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { ResetPasswordDialogComponent } from './reset-password-dialog.component';

describe('ResetPasswordDialogComponent', () => {
  let component: ResetPasswordDialogComponent;
  let fixture: ComponentFixture<ResetPasswordDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ResetPasswordDialogComponent ],
      providers: [
        {
          provide: MatDialogRef,
          useValue: {},
        },
      ],
      imports: [
        FormsModule,
        BrowserAnimationsModule,
        MatInputModule,
        MatDialogModule,
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResetPasswordDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
