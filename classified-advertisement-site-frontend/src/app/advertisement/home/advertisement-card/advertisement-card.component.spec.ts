import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdvertisementCardComponent } from './advertisement-card.component';
import { MatCardModule } from '@angular/material/card';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatIconModule } from '@angular/material/icon';

describe('AdvertisementCardComponent', () => {
  let component: AdvertisementCardComponent;
  let fixture: ComponentFixture<AdvertisementCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdvertisementCardComponent ],
      imports: [
        MatCardModule,
        RouterTestingModule,
        HttpClientTestingModule,
        MatIconModule,
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdvertisementCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
