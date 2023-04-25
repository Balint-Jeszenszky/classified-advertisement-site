import { Injectable } from '@nestjs/common';
import { ProductResponse } from './dto/ProductResponse.dto';
import { SiteRequest } from './dto/SiteRequest.dto';
import { SiteResponse } from './dto/SiteResponse.dto';
import { Advertisement } from './dto/Advertisement.dto';

@Injectable()
export class ScraperService {

  getPriceByAdvertisementId(id: number): ProductResponse {
    // TODO get from DB

    return undefined;
  }

  getAllSites(): SiteResponse[] {
    // TODO read all from DB

    return undefined;
  }

  createSite(site: SiteRequest): SiteResponse {
    // TODO save to database
    // TODO run scraper for this site

    return undefined;
  }

  updateSite(id: string, site: SiteRequest): SiteResponse {
    // TODO save to database
    // TODO run scraper for this site

    return undefined;
  }

  deleteSite(id: string) {
    // TODO remove from database
  }

  addAdvertisement(advertisement: Advertisement): void {
    // TODO save to database
  }

  editAdvertisement(advertisement: Advertisement): void {
    // TODO save to database
  }

  deleteAdvertisement(advertisementId: number): void {
    // TODO remove from database
  }}
