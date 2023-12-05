import { Test, TestingModule } from '@nestjs/testing';
import { HttpService } from '@nestjs/axios';
import { ApiClientService } from './api-client.service';

describe('ApiClientService', () => {
  let service: ApiClientService;
  let httpService: HttpService;

  beforeAll(() => {
    process.env.ADVERTISEMENT_SERVICE_INTERNAL_API_URL = 'localhost';
    process.env.ADVERTISEMENT_SERVICE_ADVERTISEMENT_EXISTS_PATH = 'path';
  });

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [ApiClientService],
    }).useMocker((token) => {
      if (token === HttpService) {
        return {
          axiosRef: {
            get: jest.fn(),
          },
        };
      }
    }).compile();

    httpService = module.get<HttpService>(HttpService);
    service = module.get<ApiClientService>(ApiClientService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  it('should return advertiser id from api response', async () => {
    const id = 1;
    const advertiserId = 2;
    const response = {
      data: {
        advertiserId,
      },
    };
    const httpGetSpy = jest.spyOn(httpService.axiosRef, 'get').mockImplementation((path) => {
      expect(path).toBe(`${process.env.ADVERTISEMENT_SERVICE_INTERNAL_API_URL}${process.env.ADVERTISEMENT_SERVICE_ADVERTISEMENT_EXISTS_PATH}/${id}`);
      return response as any;
    });

    const result = await service.advertisementExistsById(id);

    expect(httpGetSpy).toBeCalled();
    expect(result).toBe(advertiserId);
  });

  it('should return advertiser id from api response', async () => {
    const httpGetSpy = jest.spyOn(httpService.axiosRef, 'get').mockImplementation(() => {throw new Error()});

    const result = await service.advertisementExistsById(1);

    expect(httpGetSpy).toBeCalled();
    expect(result).toBe(undefined);
  });
});
