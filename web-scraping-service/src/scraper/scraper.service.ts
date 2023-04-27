import { Injectable, Logger, NotFoundException } from '@nestjs/common';
import { ProductResponse } from './dto/ProductResponse.dto';
import { SiteRequest } from './dto/SiteRequest.dto';
import { SiteResponse } from './dto/SiteResponse.dto';
import { Advertisement } from './dto/Advertisement.dto';
import { Site, SiteDocument } from './schemas/site.schema';
import { Model } from 'mongoose';
import { InjectModel } from '@nestjs/mongoose';
import { Price } from './schemas/price.schema';
import puppeteer from 'puppeteer-extra';
import { Browser, executablePath } from 'puppeteer';
import StealthPlugin from 'puppeteer-extra-plugin-stealth';
import UserAgent from 'user-agents';
import { Cron } from '@nestjs/schedule';

@Injectable()
export class ScraperService {
  private readonly logger: Logger = new Logger(ScraperService.name);

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
    this.logger.log(`New ad ${JSON.stringify(advertisement)}`);
    // TODO save to database
  }

  editAdvertisement(advertisement: Advertisement): void {
    this.logger.log(`update ad ${JSON.stringify(advertisement)}`);
    // TODO save to database
  }

  deleteAdvertisement(advertisementId: number): void {
    this.logger.log(`delete ad ${advertisementId}`);
    // TODO remove from database
  }

  @Cron('0 3 * * *')
  private async scrapePriceForAllAds() {
    this.logger.log('Scraper started');
    const categoryIds = await this.priceModel.distinct('categoryId').exec() as number[];

    puppeteer.use(StealthPlugin());
    const browser = await puppeteer.launch({ executablePath: executablePath() });

    await Promise.all(categoryIds.map(async id => await this.scrapeCategory(id, browser)));

    await browser.close();
    this.logger.log('Scraper finished');
  }

  private async scrapeCategory(categoryId: number, browser: Browser) {
    this.logger.log(`Scrape category ${categoryId}`);
    const sites = await this.siteModel.find({ categoryId }).exec();
    const advertisements = await this.priceModel.find({ categoryId }).exec();

    const updatedAdvertisements = await Promise.all(advertisements.map(async advertisement => {

      try {
        const results = await this.scrapeSites(sites, advertisement.originalTitle, browser);
        const bestResult = results.reduce((prev, curr) => prev.price < curr.price ? prev : curr);

        advertisement.site = bestResult.site;
        advertisement.image = bestResult.image;
        advertisement.title = bestResult.title;
        advertisement.url = bestResult.url;
        advertisement.price = bestResult.price;
      } catch {
        this.logger.log(`No results for ${advertisement.originalTitle} (${advertisement.id})`);
      }

      return advertisement;
    }));

    this.priceModel.bulkSave(updatedAdvertisements);
  }

  private async scrapeSites(sites: Site[], searchTerm: string, browser: Browser) {
    const page = await browser.newPage();
    const userAgent = new UserAgent();
    await page.setUserAgent(userAgent.toString());
    await page.setViewport({ width: 1144, height: 969, isLandscape: true });

    const results = await Promise.all(sites.map(async site => {
      this.logger.log(`Scraping ${searchTerm} from ${site.name}`);

      await page.goto(site.url.replace('{{searchTerm}}', searchTerm), { waitUntil: 'networkidle0' });
    
      return await page.evaluate(site => {
        const product = document.querySelector(site.selector.base);
    
        if (!product) {
          return undefined;
        }

        const getProperty = (element, property) =>  element[property] ?? element.getAttribute(property);
    
        const price = getProperty(product.querySelector(site.selector.price.selector), site.selector.price.property);
    
        return ({
          site: site.name,
          image: getProperty(product.querySelector(site.selector.image.selector), site.selector.image.property),
          title: getProperty(product.querySelector(site.selector.title.selector), site.selector.title.property),
          url: getProperty(product.querySelector(site.selector.title.selector), site.selector.url.property),
          price: parseFloat(price.substring(price.search(/\d/)).replaceAll(' ', '').replaceAll('\u00a0', '')),
        });
      }, site);
    }));

    await page.close();

    return results;
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
