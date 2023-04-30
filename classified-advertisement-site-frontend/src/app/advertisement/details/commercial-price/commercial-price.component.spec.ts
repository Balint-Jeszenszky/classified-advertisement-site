import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommercialPriceComponent } from './commercial-price.component';

describe('CommercialPriceComponent', () => {
  let component: CommercialPriceComponent;
  let fixture: ComponentFixture<CommercialPriceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CommercialPriceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CommercialPriceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
