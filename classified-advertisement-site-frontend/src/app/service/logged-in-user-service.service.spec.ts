import { TestBed } from '@angular/core/testing';

import { LoggedInUserServiceService } from './logged-in-user-service.service';

describe('LoggedInUserServiceService', () => {
  let service: LoggedInUserServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoggedInUserServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
