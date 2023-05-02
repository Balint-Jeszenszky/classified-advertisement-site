import { Test, TestingModule } from '@nestjs/testing';
import { ScraperController } from './scraper.controller';
import { ScraperService } from './scraper.service';
import { getModelToken } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Site } from './schemas/site.schema';
import { Product } from './schemas/product.schema';

describe('ScraperController', () => {
  let controller: ScraperController;

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
      controllers: [ScraperController],
    }).compile();

    controller = module.get<ScraperController>(ScraperController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
