import { gql } from 'apollo-angular';

export const GET_CHATS_FOR_USER = gql`
  query GetChatsForUser {
    chatsForUser {
      id
      advertisementId
      advertisementOwnerUserId
      fromUserId
      messages {
        createdAt
        text
        userId
      }
    }
  }
`;
