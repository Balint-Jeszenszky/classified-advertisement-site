import { Test, TestingModule } from '@nestjs/testing';
import { getModelToken } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { ModuleMocker, MockFunctionMetadata } from 'jest-mock';
import { generateVAPIDKeys } from 'web-push';
import { NotificationController } from './notification.controller';
import { NotificationService } from './notification.service';
import { PushSubscription } from './schema/push-subscription.model';

describe('NotificationController', () => {
  let controller: NotificationController;
  
  beforeAll(() => {
    const { publicKey, privateKey } = generateVAPIDKeys();
    process.env.VAPID_PUBLIC_KEY = publicKey;
    process.env.VAPID_PRIVATE_KEY = privateKey;
  });

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        NotificationService,
        { 
          provide: getModelToken(PushSubscription.name),
          useValue: Model,
        },
      ],
        controllers: [NotificationController],
    }).useMocker((token) => {
      const moduleMocker = new ModuleMocker(global);
      const mockMetadata = moduleMocker.getMetadata(token) as MockFunctionMetadata<any, any>;
      const Mock = moduleMocker.generateFromMetadata(mockMetadata);
      return new Mock();
    }).compile();

    controller = module.get<NotificationController>(NotificationController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
