
/*
 * -------------------------------------------------------
 * THIS FILE WAS AUTOMATICALLY GENERATED (DO NOT MODIFY)
 * -------------------------------------------------------
 */

/* tslint:disable */
/* eslint-disable */

export class NewChatMessage {
    chatId: number;
    text: string;
}

export class NewAdvertisementMessage {
    advertisementId: number;
    text: string;
}

export class Chat {
    id: number;
    advertisementId: number;
    advertisementOwnerUserId: number;
    fromUserId: number;
    messages: Message[];
}

export class Message {
    id: number;
    text: string;
    userId: number;
    createdAt: Date;
}

export abstract class IQuery {
    abstract chat(id: number): Nullable<Chat> | Promise<Nullable<Chat>>;

    abstract chatsForUser(): Chat[] | Promise<Chat[]>;
}

export abstract class IMutation {
    abstract sendMessageToChat(newMessage: NewChatMessage): Nullable<Message> | Promise<Nullable<Message>>;

    abstract sendMessageForAdvertisement(newMessage: NewAdvertisementMessage): Nullable<Message> | Promise<Nullable<Message>>;
}

type Nullable<T> = T | null;
