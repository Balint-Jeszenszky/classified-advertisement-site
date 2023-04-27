import { Injectable, NotFoundException } from '@nestjs/common';
import { ProductResponse } from './dto/ProductResponse.dto';
import { SiteRequest } from './dto/SiteRequest.dto';
import { SiteResponse } from './dto/SiteResponse.dto';
import { Advertisement } from './dto/Advertisement.dto';
import { Site, SiteDocument } from './schemas/site.schema';
import { Model } from 'mongoose';
import { InjectModel } from '@nestjs/mongoose';
import { Price } from './schemas/price.schema';

@Injectable()
export class ScraperService {

  constructor(
    @InjectModel(Site.name) private readonly siteModel: Model<Site>,
    @InjectModel(Price.name) private readonly priceModel: Model<Price>,
  ) { }

  async getPriceByAdvertisementId(id: number): Promise<ProductResponse> {
    const price = await this.priceModel.findOne({ advertisementId: id }).exec();

    if (!price) {
      throw new NotFoundException;
    }

    return price;
  }

  async getAllSites(): Promise<SiteResponse[]> {
    const sites = await this.siteModel.find().exec();

    return this.mapSitesModelToDto(sites);
  }

  async createSite(site: SiteRequest): Promise<SiteResponse> {
    const createdSite = new this.siteModel(site);
    await createdSite.save();

    // TODO run scraper for this site

    return this.mapSiteModelToDto(createdSite);
  }

  async updateSite(id: string, site: SiteRequest): Promise<SiteResponse> {

    const updatedSite = await this.siteModel.findByIdAndUpdate(id, site, { new: true }).exec();

    if (!updatedSite) {
      throw new NotFoundException;
    }

    // TODO run scraper for this site

    return this.mapSiteModelToDto(updatedSite);
  }

  async deleteSite(id: string): Promise<void> {
    await this.siteModel.deleteOne({ _id: id }).exec();
  }

  addAdvertisement(advertisement: Advertisement): void {
    // TODO save to database
  }

  editAdvertisement(advertisement: Advertisement): void {
    // TODO save to database
  }

  deleteAdvertisement(advertisementId: number): void {
    // TODO remove from database
  }

  private mapSitesModelToDto(sites: SiteDocument[]): SiteResponse[] {
    return sites.map(site => this.mapSiteModelToDto(site));
  }

  private mapSiteModelToDto(site: SiteDocument): SiteResponse {
    return {
      id: site._id.toHexString(),
      name: site.name,
      url: site.url,
      categoryId: site.categoryId,
      selector: site.selector,
    }
  }
}
