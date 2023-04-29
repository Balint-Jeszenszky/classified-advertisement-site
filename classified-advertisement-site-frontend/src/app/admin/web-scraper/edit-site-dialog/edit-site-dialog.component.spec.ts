import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { EditSiteDialogComponent } from './edit-site-dialog.component';

describe('EditSiteDialogComponent', () => {
  let component: EditSiteDialogComponent;
  let fixture: ComponentFixture<EditSiteDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditSiteDialogComponent ],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: {} },
        { provide: MatDialogRef, useValue: {} },
      ],
      imports: [
        MatFormFieldModule,
        MatSelectModule,
        FormsModule,
        MatDialogModule,
        MatInputModule,
        BrowserAnimationsModule,
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditSiteDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
