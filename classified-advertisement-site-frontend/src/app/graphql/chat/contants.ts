import { FetchPolicy, InMemoryCache } from "@apollo/client/core";

export const URI = '/api/chat/graphql';

export const APOLLO_DEFAULTS = {
  cache: new InMemoryCache(),
  defaultOptions: {
    query: {
      fetchPolicy: 'no-cache' as FetchPolicy,
    },
  },
};
