import { APOLLO_OPTIONS, ApolloModule } from 'apollo-angular';
import { HttpLink } from 'apollo-angular/http';
import { NgModule } from '@angular/core';
import { ApolloClientOptions } from '@apollo/client/core';
import { URI, APOLLO_DEFAULTS } from './contants';


export function createApollo(httpLink: HttpLink): ApolloClientOptions<any> {
  return {
    ...APOLLO_DEFAULTS,
    link: httpLink.create({ uri: URI }),
  };
}

@NgModule({
  exports: [ApolloModule],
  providers: [
    {
      provide: APOLLO_OPTIONS,
      useFactory: createApollo,
      deps: [HttpLink],
    },
  ],
})
export class GraphQLModule {}
