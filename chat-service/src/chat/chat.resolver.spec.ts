import { Test, TestingModule } from '@nestjs/testing';
import { ChatResolver } from './chat.resolver';
import { ChatService } from './chat.service';
import { ApiClientService } from './api-client.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import Chat from './entity/chat.entity';
import Message from './entity/message.entity';
import { HttpService } from '@nestjs/axios';

describe('ChatResolver', () => {
  let resolver: ChatResolver;

  beforeAll(() => {
    process.env.ADVERTISEMENT_SERVICE_INTERNAL_API_URL = 'localhost';
    process.env.ADVERTISEMENT_SERVICE_ADVERTISEMENT_EXISTS_PATH = 'path';
  });

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ChatResolver,
        ChatService,
        ApiClientService,
        {
          provide: getRepositoryToken(Chat),
          useValue: Repository<Chat>,
        },
        {
          provide: getRepositoryToken(Message),
          useValue: Repository<Message>,
        },
        {
          provide: 'REALTIME_CHAT_SERVICE',
          useValue: {},
        },
        { 
          provide: 'PUSH_NOTIFICATION',
          useValue: {},
        },
      ],
    }).useMocker((token) => {
      if (token === HttpService) {
        return {
          axiosRef: {
            get: () => ({
              data: {
                advertiserId: 1,
              },
            }),
          },
        };
      }
    }).compile();

    resolver = module.get<ChatResolver>(ChatResolver);
  });

  it('should be defined', () => {
    expect(resolver).toBeDefined();
  });
});
