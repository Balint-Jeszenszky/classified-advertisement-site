import { HttpService } from '@nestjs/axios';
import { Injectable } from '@nestjs/common';

@Injectable()
export class ApiClientService {
  private readonly advertisementServicePath: string;

  constructor(
    private readonly httpService: HttpService,
  ) {
    if (!process.env.ADVERTISEMENT_SERVICE_ADVERTISEMENT_EXISTS_PATH || !process.env.ADVERTISEMENT_SERVICE_ADVERTISEMENT_EXISTS_PATH) {
      throw new Error('Advertisement service url or path not set');
    }

    this.advertisementServicePath = process.env.ADVERTISEMENT_SERVICE_INTERNAL_API_URL + process.env.ADVERTISEMENT_SERVICE_ADVERTISEMENT_EXISTS_PATH;
  }

  async advertisementExistsById(id: number): Promise<number | undefined> {
    try {
      const advertisement = await this.httpService.axiosRef.get<{advertiserId: number}>(`${this.advertisementServicePath}/${id}`);
      return advertisement.data.advertiserId;
    } catch {
      return undefined;
    }
  }
}
