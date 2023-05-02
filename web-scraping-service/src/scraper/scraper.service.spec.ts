import { Test, TestingModule } from '@nestjs/testing';
import { ScraperService } from './scraper.service';
import { getModelToken } from '@nestjs/mongoose';
import { Site } from './schemas/site.schema';
import { Product } from './schemas/product.schema';

describe('ScraperService', () => {
  let service: ScraperService;

  beforeEach(async () => {
    const siteMockRepository = {
      find: () => {
        return {exec: jest.fn(() => {
          return [{
            _id: {toHexString: jest.fn(() => 'id1')},
            name: 'Site 1',
            url: 'url',
            categoryId: 3,
            selector: {},
          }, {
            _id: {toHexString: jest.fn(() => 'id2')},
            name: 'Site 2',
            url: 'url',
            categoryId: 2,
            selector: {},
          }]
        })};
      },
    };

    const productMockRepository = {
      findOne: (advertisementId: number) => {
        return {
          exec: jest.fn(() => ({
            advertisementId,
            categoryId: 5,
            productTitle: 'Product',
          }),
        )};
      },
    };

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ScraperService,
        { 
          provide: getModelToken(Site.name),
          useValue: siteMockRepository,
        },
        { 
          provide: getModelToken(Product.name),
          useValue: productMockRepository,
        },
      ],
    }).compile();

    service = module.get<ScraperService>(ScraperService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  it('getProductByAdvertisementId() should return requested product', async () => {
    const result = await service.getProductByAdvertisementId(5);
    expect(result.title).toEqual('Product');
  });

  it('getAllSites() should return all sites', async () => {
    const result = await service.getAllSites();
    expect(result).toHaveLength(2);
    expect(result[0].name).toEqual('Site 1');
    expect(result[0].id).toEqual('id1');
    expect(result[1].name).toEqual('Site 2');
    expect(result[1].id).toEqual('id2');
  });
});
