import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { LoggedInUserService } from './logged-in-user.service';

describe('LoggedInUserServiceService', () => {
  let service: LoggedInUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ]
    });
    service = TestBed.inject(LoggedInUserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
