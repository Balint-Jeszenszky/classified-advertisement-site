import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from './../src/app.module';
import { getRepositoryToken } from '@nestjs/typeorm';
import Chat from '../src/chat/entity/chat.entity';
import { Repository } from 'typeorm';
import Message from '../src/chat/entity/message.entity';

const GRAPHWL_API = '/api/graphql';

describe('Chat resolver (e2e)', () => {
  let app: INestApplication;
  let moduleFixture: TestingModule;
  let createdAt: Date;

  beforeEach(async () => {
    moduleFixture = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    await app.init();

    const chatRepository: Repository<Chat> = await moduleFixture.resolve(getRepositoryToken(Chat));
    const messageRepository: Repository<Message> = await moduleFixture.resolve(getRepositoryToken(Message));

    const chats = await chatRepository.save([
      {
        id: 1,
        advertisementId: 1,
        advertisementOwnerUserId: 1,
        fromUserId: 2,
      },
      {
        id: 2,
        advertisementId: 1,
        advertisementOwnerUserId: 1,
        fromUserId: 3,
      }
    ]);

    createdAt = new Date();

    await messageRepository.save([
      {
        text: 'message',
        userId: 2,
        createdAt: createdAt,
        chat: chats[0],
      },
      {
        text: 'message',
        userId: 3,
        createdAt: createdAt,
        chat: chats[1],
      }
    ]);
  });

  afterEach(async () => {
    const messageRepository: Repository<Message> = await moduleFixture.resolve(getRepositoryToken(Message));
    await messageRepository.createQueryBuilder().delete().execute();
    const chatRepository: Repository<Chat> = await moduleFixture.resolve(getRepositoryToken(Chat));
    await chatRepository.createQueryBuilder().delete().execute();
  });

  it('throws error when unauthenticated user lists chats', () => {
    return request(app.getHttpServer())
      .post(GRAPHWL_API)
      .send({
        operationName: 'GetChatsForUser',
        variables: {},
        query: getChatsForUserQuery,
      })
      .expect(200)
      .expect(({body}) => {
        expect(body.data).toBeNull();
        expect(body.errors[0].message).toEqual('Unauthorized');
      });
  });

  it('returns list when authenticated user lists chats', () => {
    return request(app.getHttpServer())
      .post(GRAPHWL_API)
      .set('x-user-data', userAuthHeader)
      .send({
        operationName: 'GetChatsForUser',
        variables: {},
        query: getChatsForUserQuery,
      })
      .expect(200)
      .expect(({body}) => {
        expect(body.data.chatsForUser).toStrictEqual([{
          advertisementId: 1,
          advertisementOwnerUserId: 1,
          fromUserId: 2,
          id: 1,
          messages: [
            {
              createdAt: createdAt.toISOString(),
              text: 'message',
              userId: 2,
            },
          ],
        }]);
        expect(body.errors).toBeUndefined();
      });
  });

  it('throws error when unauthenticated user queries chat by id', () => {
    return request(app.getHttpServer())
      .post(GRAPHWL_API)
      .send({
        operationName: 'GetChat',
        variables: {
            id: 1
        },
        query: getChat,
      })
      .expect(200)
      .expect(({body}) => {
        expect(body.data.chat).toBeNull();
        expect(body.errors[0].message).toEqual('Unauthorized');
      });
  });

  it('throws error when authenticated user queries other user\'s chat by id', () => {
    return request(app.getHttpServer())
      .post(GRAPHWL_API)
      .set('x-user-data', userAuthHeader)
      .send({
        operationName: 'GetChat',
        variables: {
            id: 2
        },
        query: getChat,
      })
      .expect(200)
      .expect(({body}) => {
        expect(body.data.chat).toBeNull();
        expect(body.errors[0].message).toEqual('Not Found');
      });
  });

  it('returns chat for authenticated user when queries chat by id', () => {
    return request(app.getHttpServer())
      .post(GRAPHWL_API)
      .set('x-user-data', userAuthHeader)
      .send({
        operationName: 'GetChat',
        variables: {
            id: 1
        },
        query: getChat,
      })
      .expect(200)
      .expect(({body}) => {
        expect(body.data.chat).toEqual({
          id: 1,
          advertisementId: 1,
          advertisementOwnerUserId: 1,
          fromUserId: 2,
          messages: [
            {
              createdAt: createdAt.toISOString(),
              text: 'message',
              userId: 2,
            }
          ],
        });
        expect(body.errors).toBeUndefined();
      });
  });

  it('throws error when unauthenticated user queries chat by advertisement id', () => {
    return request(app.getHttpServer())
      .post(GRAPHWL_API)
      .send({
        operationName: 'GetChatByAdvertisement',
        variables: {
            advertisementId: 1,
        },
        query: getChatByAdvertisement,
      })
      .expect(200)
      .expect(({body}) => {
        expect(body.data.chatIdByAdvertisement).toBeNull();
        expect(body.errors[0].message).toEqual('Unauthorized');
      });
  });

  it('returns chat for authenticated user when queries chat by advertisement id', () => {
    return request(app.getHttpServer())
      .post(GRAPHWL_API)
      .set('x-user-data', userAuthHeader)
      .send({
        operationName: 'GetChatByAdvertisement',
        variables: {
            advertisementId: 1,
        },
        query: getChatByAdvertisement,
      })
      .expect(200)
      .expect(({body}) => {
        expect(body.data.chatIdByAdvertisement).toEqual({
          id: 1,
        });
        expect(body.errors).toBeUndefined();
      });
  });

  it('throws error when unauthenticated user sends message to chat by advertisement id', () => {
    return request(app.getHttpServer())
      .post(GRAPHWL_API)
      .send({
        operationName: 'SendMessageForAdvertisement',
        variables: {
          newMessage: {
            advertisementId: 1,
            text: 'Hi!'
          }
        },
        query: sendMessageForAdvertisement,
      })
      .expect(200)
      .expect(({body}) => {
        expect(body.data.sendMessageForAdvertisement).toBeNull();
        expect(body.errors[0].message).toEqual('Unauthorized');
      });
  });

  it('returns message when user sends message to chat by advertisement id', () => {
    return request(app.getHttpServer())
      .post(GRAPHWL_API)
      .set('x-user-data', userAuthHeader)
      .send({
        operationName: 'SendMessageForAdvertisement',
        variables: {
          newMessage: {
            advertisementId: 1,
            text: 'Hi!'
          }
        },
        query: sendMessageForAdvertisement,
      })
      .expect(200)
      .expect(({body}) => {
        expect(body.data.sendMessageForAdvertisement).toEqual({
          text: 'Hi!',
          userId: 2,
        });
        expect(body.errors).toBeUndefined();
      });
  });
});

const userAuthHeader =  'eyJ1c2VybmFtZSI6InVzZXIiLCJpZCI6MiwiZW1haWwiOiJ1c2VyQHVzZXIubG9jYWwiLCJyb2xlcyI6WyJST0xFX1VTRVIiXX0=';

const getChatsForUserQuery = `
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

const getChat = `
query GetChat($id: Int!) {
  chat(id: $id) {
    id
    advertisementId
    advertisementOwnerUserId
    fromUserId
    messages {
      text
      userId
      createdAt
    }
  }
}
`;

const getChatByAdvertisement = `
query GetChatByAdvertisement($advertisementId: Int!) {
  chatIdByAdvertisement(advertisementId: $advertisementId) {
    id
  }
}
`;

const sendMessageForAdvertisement  = `
mutation SendMessageForAdvertisement($newMessage: NewAdvertisementMessage!) {
  sendMessageForAdvertisement(newMessage: $newMessage) {
    text
    userId
  }
}
`;
