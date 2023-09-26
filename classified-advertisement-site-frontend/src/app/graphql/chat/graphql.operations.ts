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

export const GET_CHAT = gql`
  query GetChat($id: Int!) {
    chat(id: $id) {
      id
      advertisementId
      advertisementOwnerUserId
      fromUserId
      messages {
        id
        text
        userId
        createdAt
      }
    }
  }
`;

export const SEND_MESSAGE_TO_CHAT = gql`
  mutation SendMessageToChat($newMessage: NewChatMessage!) {
    sendMessageToChat(newMessage: $newMessage) {
      id
      text
      userId
      createdAt
    }
  }
`;

export const SEND_MESSAGE_FOR_ADVERTISEMENT = gql`
  mutation SendMessageForAdvertisement($newMessage: NewAdvertisementMessage!) {
    sendMessageForAdvertisement(newMessage: $newMessage) {
      id
      text
      userId
      createdAt
    }
  }
`;
