import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';

import { WebScraperComponent } from './web-scraper.component';

describe('WebScraperComponent', () => {
  let component: WebScraperComponent;
  let fixture: ComponentFixture<WebScraperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WebScraperComponent ],
      imports: [
        HttpClientTestingModule,
        MatDialogModule,
        MatTableModule,
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(WebScraperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
