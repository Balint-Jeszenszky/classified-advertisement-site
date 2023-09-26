import { Injectable } from '@angular/core';
import { HttpLink } from 'apollo-angular/http';
import { Apollo } from 'apollo-angular';
import { HttpHeaders } from '@angular/common/http';
import { URI, APOLLO_DEFAULTS } from './chat/contants';

@Injectable({
  providedIn: 'root'
})
export class GraphqlCredentialsService {

  constructor(
    private readonly apollo: Apollo,
    private readonly httpLink: HttpLink,
  ) { }

  updateCredentials(token: string) {
    this.apollo.removeClient();
    this.apollo.createDefault({
      ...APOLLO_DEFAULTS,
      link: this.httpLink.create({
        uri: URI,
        headers: new HttpHeaders({ Authorization: `Bearer ${token}`}),
      }),
    });
  }
}
