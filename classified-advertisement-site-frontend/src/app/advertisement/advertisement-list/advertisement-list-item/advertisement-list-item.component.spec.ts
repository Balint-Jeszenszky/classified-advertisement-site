import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdvertisementListItemComponent } from './advertisement-list-item.component';

describe('AdvertisementListItemComponent', () => {
  let component: AdvertisementListItemComponent;
  let fixture: ComponentFixture<AdvertisementListItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdvertisementListItemComponent ],
      imports: [
        HttpClientTestingModule,
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdvertisementListItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
