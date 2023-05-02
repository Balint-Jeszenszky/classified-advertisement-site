import { Test, TestingModule } from '@nestjs/testing';
import { MessageQueueController } from './message-queue.controller';
import { ScraperService } from './scraper.service';
import { getModelToken } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Site } from './schemas/site.schema';
import { Product } from './schemas/product.schema';

describe('MessageQueueController', () => {
  let controller: MessageQueueController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ScraperService,
        { 
          provide: getModelToken(Site.name),
          useValue: Model,
        },
        { 
          provide: getModelToken(Product.name),
          useValue: Model,
        },
      ],
      controllers: [MessageQueueController],
    }).compile();

    controller = module.get<MessageQueueController>(MessageQueueController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
