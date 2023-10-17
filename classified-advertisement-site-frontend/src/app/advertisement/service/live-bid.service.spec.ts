import { TestBed } from '@angular/core/testing';

import { LiveBidService } from './live-bid.service';

describe('LiveBidService', () => {
  let service: LiveBidService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LiveBidService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
