import { Test, TestingModule } from '@nestjs/testing';
import { ChatController } from './chat.controller';
import { ChatService } from './chat.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import Chat from './entity/chat.entity';
import { Repository } from 'typeorm';
import Message from './entity/message.entity';
import { ApiClientService } from './api-client.service';
import { HttpService } from '@nestjs/axios';

describe('ChatController', () => {
  let controller: ChatController;

  beforeAll(() => {
    process.env.ADVERTISEMENT_SERVICE_INTERNAL_API_URL = 'localhost';
    process.env.ADVERTISEMENT_SERVICE_ADVERTISEMENT_EXISTS_PATH = 'path';
  });

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
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
      controllers: [ChatController],
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

    controller = module.get<ChatController>(ChatController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
