import { Test, TestingModule } from '@nestjs/testing';
import { getModelToken } from '@nestjs/mongoose';
import { MailerService,  } from '@nestjs-modules/mailer';
import { Model } from 'mongoose';
import { generateVAPIDKeys } from 'web-push';
import { NotificationService } from './notification.service';
import { PushSubscription } from './schema/push-subscription.model';
import { Push } from './dto/Notification.dto';

describe('NotificationService', () => {
  let service: NotificationService;
  let mockModel: Model<PushSubscription>;
  
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
          useValue: Model<PushSubscription>,
        },
      ],
    }).useMocker((token) => {
      if (token === MailerService) {
        return { sendMail: jest.fn().mockImplementation((data) => data) };
      }
      console.log(token)
    }).compile();

    mockModel = module.get<Model<PushSubscription>>(getModelToken(PushSubscription.name));
    service = module.get<NotificationService>(NotificationService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  it('should do nothing with not found user', async () => {
    const payload: Push = {
      userId: 1,
      template: 'chatMessage',
      data: {
        message: 'Hi!',
      },
    };

    const findOneSpy = jest.spyOn(mockModel, 'findOne').mockImplementation(({ userId }) => {
      expect(userId).toBe(payload.userId);
      return {
        exec: jest.fn(() => undefined),
      } as any;
    });

    await service.sendWebPushNotification(payload);

    expect(findOneSpy).toBeCalled();
  });

  it('should delete wrong endpoint', async () => {
    const payload: Push = {
      userId: 1,
      template: 'chatMessage',
      data: {
        message: 'Hi!',
      },
    };

    const user = {
      subscriptions: [{}],
      save: jest.fn(() => {
        expect(user.subscriptions.length).toBe(0);
      }),
    };

    const findOneSpy = jest.spyOn(mockModel, 'findOne').mockImplementation(({ userId }) => {
      expect(userId).toBe(payload.userId);
      return {
        exec: jest.fn(() => user),
      } as any;
    });

    await service.sendWebPushNotification(payload);

    expect(findOneSpy).toBeCalled();
    expect(user.save).toBeCalled();
  });

  it('should return public vapid key', () => {
    const publicKey = service.getPublicVapidKey();

    expect(publicKey).toBe(process.env.VAPID_PUBLIC_KEY);
  });

  it('should do nothing if endpoint already subscribed', async () => {
    const user = {
      id: 1,
    };

    const savedSubscription = {};

    const savedUser = {
      subscriptions: [savedSubscription],
      save: jest.fn(() => savedUser),
    };

    const findOneSpy = jest.spyOn(mockModel, 'findOne').mockImplementation(({ userId }) => {
      expect(userId).toBe(user.id);
      return {
        exec: jest.fn(() => savedUser),
      } as any;
    });

    await service.subscribePushNotification(user as any, savedSubscription as any);

    expect(findOneSpy).toBeCalled();
    expect(savedUser.save).not.toBeCalled();
    expect(savedUser.subscriptions.length).toBe(1);
  });

  it('should save subscription if not exists', async () => {
    const user = {
      id: 1,
    };

    const subscription = {
      endpoint: 'endpoint',
    };

    const savedUser = {
      subscriptions: [],
      save: jest.fn(() => savedUser),
    };

    const findOneSpy = jest.spyOn(mockModel, 'findOne').mockImplementation(({ userId }) => {
      expect(userId).toBe(user.id);
      return {
        exec: jest.fn(() => savedUser),
      } as any;
    });

    await service.subscribePushNotification(user as any, subscription as any);

    expect(findOneSpy).toBeCalled();
    expect(savedUser.save).toBeCalled();
    expect(savedUser.subscriptions.length).toBe(1);
  });

  it('should send email', async () => {
    process.env.EMAIL_USER = 'admin@test.local';
    process.env.BASEURL = 'localhost';

    const payload = {
      toAddress: 'test@test.local',
      template: 'winnerBid',
      data: {
        user: 'user',
        advertisementTitle: 'Example advertisement',
        advertisementId: 1,
      },
    };

    const result = await service.sendEmail(payload);

    expect(result.from).toBe(process.env.EMAIL_USER);
    expect(result.subject).toBe('Your bid won for the Example advertisement advertisement');
    expect(result.to).toBe(payload.toAddress);
    expect(result.template).toBe(payload.template);
    expect(result.context).toStrictEqual({...payload.data, baseurl: process.env.BASEURL});
  });
});
