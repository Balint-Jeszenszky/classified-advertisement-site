import { Test, TestingModule } from '@nestjs/testing';
import { ChatService } from './chat.service';
import { ApiClientService } from './api-client.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import Chat from './entity/chat.entity';
import Message from './entity/message.entity';
import { BadRequestException, NotFoundException } from '@nestjs/common';
import { ClientProxy, ReadPacket, WritePacket } from '@nestjs/microservices';

class MockClientProxy extends ClientProxy {
  connect(): Promise<any> { return undefined }
  close() { }
  protected publish(packet: ReadPacket<any>, callback: (packet: WritePacket<any>) => void): () => void { return undefined }
  protected dispatchEvent<T = any>(packet: ReadPacket<any>): Promise<T> { return undefined }
}

describe('ChatService', () => {
  let service: ChatService;
  let mockChatRepository: Repository<Chat>;
  let mockMessageRepository: Repository<Message>;
  let mockRedisClient: ClientProxy;
  let mockRMQClient: ClientProxy;
  let mockApiClient: ApiClientService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ChatService,
        {
          provide: getRepositoryToken(Chat),
          useValue: new Repository<Chat>(Chat, undefined),
        },
        {
          provide: getRepositoryToken(Message),
          useValue: new Repository<Message>(Message, undefined),
        },
        {
          provide: 'REALTIME_CHAT_SERVICE',
          useValue: new MockClientProxy(),
        },
        { 
          provide: 'PUSH_NOTIFICATION',
          useValue: new MockClientProxy(),
        },
      ],
    }).useMocker((token) => {
      if (token === ApiClientService) {
        return  {
          advertisementExistsById: jest.fn(),
        };
      }
    }).compile();

    mockChatRepository = module.get<Repository<Chat>>(getRepositoryToken(Chat));
    mockMessageRepository = module.get<Repository<Message>>(getRepositoryToken(Message));
    mockRedisClient = module.get<ClientProxy>('REALTIME_CHAT_SERVICE');
    mockRMQClient = module.get<ClientProxy>('PUSH_NOTIFICATION');
    mockApiClient = module.get<ApiClientService>(ApiClientService);
    service = module.get<ChatService>(ChatService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  it('should return chats for loged in user', async () => {
    const chats = [{ id: 1 }, { id: 2 }];
    const findChatSpy = jest.spyOn(mockChatRepository, 'find').mockImplementation(() => [...chats] as any);
    const findOneMessageSpy = jest.spyOn(mockMessageRepository, 'findOne').mockImplementation((query: any): any => {
      expect(chats.includes(query.where.chat)).toBe(true);
      return {
        createdAt: new Date(`2023-01-01 00:00:0${query.where.chat.id}`),
      };
    });

    const result = await service.getChatsForUser(1);

    expect(findChatSpy).toBeCalled();
    expect(findOneMessageSpy).toBeCalledTimes(2);
    expect(result[0].id).toBe(chats[1].id);
    expect(result[1].id).toBe(chats[0].id);
  });

  it('should return chat by id', async () => {
    const findSpy = jest.spyOn(mockChatRepository, 'findOne').mockImplementation((): any => ({ id: 1 }));

    const result = await service.getChatById(1, 1);

    expect(result.id).toBe(1);
    expect(findSpy).toBeCalled();
  });

  it('should throw exception when chat not found by id', () => {
    const findSpy = jest.spyOn(mockChatRepository, 'findOne').mockImplementation((): any => undefined);

    expect(service.getChatById(1, 1)).rejects.toEqual(new NotFoundException());

    expect(findSpy).toBeCalled();
  });

  it('should return chat by advertisement id', async () => {
    const findSpy = jest.spyOn(mockChatRepository, 'findOne').mockImplementation((): any => ({ id: 1 }));

    const result = await service.getChatById(1, 1);

    expect(result.id).toBe(1);
    expect(findSpy).toBeCalled();
  });

  it('should throw exception when chat not found by advertisement id', () => {
    const findSpy = jest.spyOn(mockChatRepository, 'findOne').mockImplementation((): any => undefined);

    expect(service.getChatById(1, 1)).rejects.toEqual(new NotFoundException());

    expect(findSpy).toBeCalled();
  });

  it('should return messages for a chat by chat id', async () => {
    const messages = [{id: 1}, {id: 2}];
    const findSpy = jest.spyOn(mockChatRepository, 'findOne').mockImplementation((): any => ({ id: 1, messages }));

    const result = await service.getMessagesByChatId(1, 1);

    expect(result).toStrictEqual(messages);
    expect(findSpy).toBeCalled();
  });

  it('should throw error when chat not found', () => {
    const findSpy = jest.spyOn(mockChatRepository, 'findOne').mockImplementation((): any => undefined);

    expect(service.getMessagesByChatId(1, 1)).rejects.toEqual(new NotFoundException());

    expect(findSpy).toBeCalled();
  });

  it ('should send message for advertisement in existing chat', async () => {
    const chat = { id: 1 };
    const userId = 1;
    const message = 'Hi!';
    const findChatSpy = jest.spyOn(mockChatRepository, 'findOne').mockImplementation((): any => chat);
    const saveChatSpy = jest.spyOn(mockChatRepository, 'save').mockImplementation((): any => {});
    const saveMessageSpy = jest.spyOn(mockMessageRepository, 'save').mockImplementation((message): any => ({ id: 1, ...message}));
    const redisSpy = jest.spyOn(mockRedisClient, 'emit').mockImplementation();
    const RMQSpy = jest.spyOn(mockRMQClient, 'emit').mockImplementation();

    const result = await service.sendMessageForAdvertisement(1, userId, message);

    expect(findChatSpy).toBeCalled();
    expect(saveChatSpy).not.toBeCalled();
    expect(saveMessageSpy).toBeCalled();
    expect(redisSpy).toBeCalled();
    expect(RMQSpy).toBeCalled();
    expect(result.userId).toBe(1);
    expect(result.text).toBe(message);
    expect(result.chat).toBe(chat);
  });

  it ('should send message for advertisement in non existing chat', async () => {
    const chatId = 1;
    const userId = 1;
    const message = 'Hi!';
    const findChatSpy = jest.spyOn(mockChatRepository, 'findOne').mockImplementation((): any => undefined);
    const saveChatSpy = jest.spyOn(mockChatRepository, 'save').mockImplementation((chat): any => ({ ...chat, id: chatId }));
    const saveMessageSpy = jest.spyOn(mockMessageRepository, 'save').mockImplementation((message): any => ({ id: 1, ...message}));
    const redisSpy = jest.spyOn(mockRedisClient, 'emit').mockImplementation();
    const RMQSpy = jest.spyOn(mockRMQClient, 'emit').mockImplementation();
    const apiClientSpy = jest.spyOn(mockApiClient, 'advertisementExistsById').mockImplementation(() => Promise.resolve(2))

    const result = await service.sendMessageForAdvertisement(1, userId, message);

    expect(findChatSpy).toBeCalled();
    expect(saveChatSpy).toBeCalled();
    expect(saveMessageSpy).toBeCalled();
    expect(redisSpy).toBeCalled();
    expect(RMQSpy).toBeCalled();
    expect(apiClientSpy).toBeCalled();
    expect(result.userId).toBe(1);
    expect(result.text).toBe(message);
    expect(result.chat.id).toBe(chatId);
  });

  it ('should throw exception for non existing advertisement', async () => {
    const userId = 1;
    const message = 'Hi!';
    const findChatSpy = jest.spyOn(mockChatRepository, 'findOne').mockImplementation((): any => undefined);
    const saveChatSpy = jest.spyOn(mockChatRepository, 'save').mockImplementation((): any => undefined);
    const saveMessageSpy = jest.spyOn(mockMessageRepository, 'save').mockImplementation((): any => undefined);
    const redisSpy = jest.spyOn(mockRedisClient, 'emit').mockImplementation();
    const RMQSpy = jest.spyOn(mockRMQClient, 'emit').mockImplementation();
    const apiClientSpy = jest.spyOn(mockApiClient, 'advertisementExistsById').mockImplementation(() => Promise.resolve(undefined))

    expect(service.sendMessageForAdvertisement(1, userId, message)).rejects.toEqual(new BadRequestException());

    expect(findChatSpy).toBeCalled();
    expect(saveChatSpy).not.toBeCalled();
    expect(saveMessageSpy).not.toBeCalled();
    expect(redisSpy).not.toBeCalled();
    expect(RMQSpy).not.toBeCalled();
    expect(apiClientSpy).not.toBeCalled();
  });

  it ('should throw exception for equal sender and advertiser', async () => {
    const userId = 1;
    const message = 'Hi!';
    const findChatSpy = jest.spyOn(mockChatRepository, 'findOne').mockImplementation((): any => undefined);
    const saveChatSpy = jest.spyOn(mockChatRepository, 'save').mockImplementation((): any => undefined);
    const saveMessageSpy = jest.spyOn(mockMessageRepository, 'save').mockImplementation((): any => undefined);
    const redisSpy = jest.spyOn(mockRedisClient, 'emit').mockImplementation();
    const RMQSpy = jest.spyOn(mockRMQClient, 'emit').mockImplementation();
    const apiClientSpy = jest.spyOn(mockApiClient, 'advertisementExistsById').mockImplementation(() => Promise.resolve(userId))

    expect(service.sendMessageForAdvertisement(1, userId, message)).rejects.toEqual(new BadRequestException());

    expect(findChatSpy).toBeCalled();
    expect(saveChatSpy).not.toBeCalled();
    expect(saveMessageSpy).not.toBeCalled();
    expect(redisSpy).not.toBeCalled();
    expect(RMQSpy).not.toBeCalled();
    expect(apiClientSpy).not.toBeCalled();
  });

  it('should send message to chat', async () => {
    const chat = { id: 1 };
    const userId = 1;
    const message = 'Hi!';
    const findChatSpy = jest.spyOn(mockChatRepository, 'findOne').mockImplementation((): any => chat);
    const saveMessageSpy = jest.spyOn(mockMessageRepository, 'save').mockImplementation((message): any => ({ id: 1, ...message}));
    const redisSpy = jest.spyOn(mockRedisClient, 'emit').mockImplementation();
    const RMQSpy = jest.spyOn(mockRMQClient, 'emit').mockImplementation();

    const result = await service.sendMessageToChat(chat.id, userId, message);

    expect(findChatSpy).toBeCalled();
    expect(saveMessageSpy).toBeCalled();
    expect(redisSpy).toBeCalled();
    expect(RMQSpy).toBeCalled();
    expect(result.userId).toBe(1);
    expect(result.text).toBe(message);
    expect(result.chat.id).toBe(chat.id);
  });

  it('should throw error for non existing chat', async () => {
    const chat = { id: 1 };
    const userId = 1;
    const message = 'Hi!';
    const findChatSpy = jest.spyOn(mockChatRepository, 'findOne').mockImplementation((): any => undefined);
    const saveMessageSpy = jest.spyOn(mockMessageRepository, 'save').mockImplementation((message): any => undefined);
    const redisSpy = jest.spyOn(mockRedisClient, 'emit').mockImplementation();
    const RMQSpy = jest.spyOn(mockRMQClient, 'emit').mockImplementation();

    expect(service.sendMessageToChat(chat.id, userId, message)).rejects.toEqual(new NotFoundException());

    expect(findChatSpy).toBeCalled();
    expect(saveMessageSpy).not.toBeCalled();
    expect(redisSpy).not.toBeCalled();
    expect(RMQSpy).not.toBeCalled();
  });

  it('should send message to connected client', () => {
    const userId = 1;
    const mockSocket = {
      data: {
        user: {
          id: userId,
        },
      },
      emit: jest.fn(),
    };
    const message = { text: 'Hi!' };

    service.addOnlineUser(mockSocket as any);
    service.sendMessageToClient(userId, message as any);

    expect(mockSocket.emit).toBeCalled();
  });

  it('should not send message for disconnected client', () => {
    const userId = 1;
    const mockSocket = {
      data: {
        user: {
          id: userId,
        },
      },
      emit: jest.fn(),
    };
    const message = { text: 'Hi!' };

    service.addOnlineUser(mockSocket as any);
    service.removeOnlineUser(mockSocket as any);
    service.sendMessageToClient(userId, message as any);

    expect(mockSocket.emit).not.toBeCalled();
  });
});
