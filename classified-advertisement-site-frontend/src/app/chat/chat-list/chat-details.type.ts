import { Chat } from "src/app/graphql/chat/generated";

export type ChatDetails = {
  chat: Chat,
  fromUsername?: string,
  advertisementTitle?: string,
};
