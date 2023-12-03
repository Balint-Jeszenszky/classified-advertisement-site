import { Test, TestingModule } from '@nestjs/testing';
import { ScraperService } from './scraper.service';
import { getModelToken } from '@nestjs/mongoose';
import { Site } from './schemas/site.schema';
import { Product } from './schemas/product.schema';
import { SiteRequest } from './dto/SiteRequest.dto';
import { NotFoundException } from '@nestjs/common';
import { ModuleMocker, MockFunctionMetadata } from 'jest-mock';

class SiteMock {
  constructor() { return SiteMock }
  static find = jest.fn();
  static save = jest.fn();
  static findByIdAndUpdate = jest.fn();
};

class ProductMock {
  static findOne = jest.fn();
};

describe('ScraperService', () => {
  let service: ScraperService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ScraperService,
        {
          provide: getModelToken(Site.name),
          useValue: SiteMock,
        },
        {
          provide: getModelToken(Product.name),
          useValue: ProductMock,
        },
      ],
    }).useMocker((token) => {
      const moduleMocker = new ModuleMocker(global);
      const mockMetadata = moduleMocker.getMetadata(token) as MockFunctionMetadata<any, any>;
      const Mock = moduleMocker.generateFromMetadata(mockMetadata);
      return new Mock();
    }).compile();

    service = module.get<ScraperService>(ScraperService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  it('getProductByAdvertisementId() should return requested product', async () => {
    jest.spyOn(ProductMock, 'findOne').mockImplementation((advertisementId: number) => {
      return {
        exec: jest.fn(() => ({
          advertisementId,
          categoryId: 5,
          productTitle: 'Product',
        }))
      };
    });
    const result = await service.getProductByAdvertisementId(5);
    expect(result.title).toEqual('Product');
  });

  it('getAllSites() should return all sites', async () => {
    jest.spyOn(SiteMock, 'find').mockImplementation(() => {
      return {
        exec: jest.fn(() => {
          return [{
            _id: { toHexString: jest.fn(() => 'id1') },
            name: 'Site 1',
            url: 'url',
            categoryId: 3,
            selector: {},
          }, {
            _id: { toHexString: jest.fn(() => 'id2') },
            name: 'Site 2',
            url: 'url',
            categoryId: 2,
            selector: {},
          }]
        })
      };
    })
    const result = await service.getAllSites();
    expect(result).toHaveLength(2);
    expect(result[0].name).toEqual('Site 1');
    expect(result[0].id).toEqual('id1');
    expect(result[1].name).toEqual('Site 2');
    expect(result[1].id).toEqual('id2');
  });

  it('createSite() should return created site', async () => {
    jest.spyOn(SiteMock, 'save').mockImplementation((site: SiteRequest) => {
      return {
        _id: { toHexString: jest.fn(() => 'newId') },
        name: 'Site 1',
        url: 'url',
        categoryId: 3,
        selector: {},
      };
    });
    const result = await service.createSite({
      name: "site",
      url: "siteurl",
      categoryIds: [1],
      selector: {
        base: "#normal-product-list .product-box-container",
        image: {
          selector: ".image img",
          property: "src"
        },
        price: {
          selector: ".price",
          property: "innerText"
        },
        title: {
          selector: ".name h2 a",
          property: "innerText"
        },
        url: {
          selector: ".name h2 a",
          property: "href"
        }
      }
    });
    expect(result.id).toEqual('newId');
  });

  it('updateSite() should return updated site data', async () => {
    jest.spyOn(SiteMock, 'findByIdAndUpdate').mockImplementation((id: string, site: SiteRequest) => {
      return {
        exec: jest.fn(() => {
          return {
            _id: { toHexString: jest.fn(() => id) },
            name: site.name,
            url: site.url,
            categoryIds: site.categoryIds,
            selector: {},
          }
        })
      };
    });
    const result = await service.updateSite('siteId', {
      name: "site",
      url: "siteurl",
      categoryIds: [1],
      selector: {
        base: "#normal-product-list .product-box-container",
        image: {
          selector: ".image img",
          property: "src"
        },
        price: {
          selector: ".price",
          property: "innerText"
        },
        title: {
          selector: ".name h2 a",
          property: "innerText"
        },
        url: {
          selector: ".name h2 a",
          property: "href"
        }
      }
    });
    expect(result.id).toEqual('siteId');
    expect(result.name).toEqual('site');
    expect(result.url).toEqual('siteurl');
    expect(result.categoryIds).toEqual([1]);
  });

  it('updateSite() should throw NotFoundException for non existing site', async () => {
    jest.spyOn(SiteMock, 'findByIdAndUpdate').mockImplementation((id: string, site: SiteRequest) => ({
      exec: jest.fn(() => {
        return undefined;
      })
    }));
    await expect(service.updateSite('notExisting', {
      name: "site",
      url: "siteurl",
      categoryIds: [1],
      selector: {
        base: "#normal-product-list .product-box-container",
        image: {
          selector: ".image img",
          property: "src"
        },
        price: {
          selector: ".price",
          property: "innerText"
        },
        title: {
          selector: ".name h2 a",
          property: "innerText"
        },
        url: {
          selector: ".name h2 a",
          property: "href"
        }
      }
    })).rejects.toEqual(new NotFoundException);
  });
});
