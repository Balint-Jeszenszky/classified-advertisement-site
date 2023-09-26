import { TestBed } from '@angular/core/testing';

import { GraphqlCredentialsService } from './graphql-credentials.service';

describe('GraphqlCredentialsService', () => {
  let service: GraphqlCredentialsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GraphqlCredentialsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
