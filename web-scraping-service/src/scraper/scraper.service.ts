import { Injectable, Logger, NotFoundException } from '@nestjs/common';
import { ProductResponse } from './dto/ProductResponse.dto';
import { SiteRequest } from './dto/SiteRequest.dto';
import { SiteResponse } from './dto/SiteResponse.dto';
import { Advertisement } from './dto/Advertisement.dto';
import { Site, SiteDocument } from './schemas/site.schema';
import { Model } from 'mongoose';
import { InjectModel } from '@nestjs/mongoose';
import { Product, ProductDocument } from './schemas/product.schema';
import puppeteer from 'puppeteer-extra';
import { Browser, executablePath } from 'puppeteer';
import StealthPlugin from 'puppeteer-extra-plugin-stealth';
import UserAgent from 'user-agents';
import { Cron } from '@nestjs/schedule';
import { ScheduleLockService } from 'src/schedule-lock/schedule-lock.service';

@Injectable()
export class ScraperService {
  private readonly logger: Logger = new Logger(ScraperService.name);

  constructor(
    @InjectModel(Site.name) private readonly siteModel: Model<Site>,
    @InjectModel(Product.name) private readonly productModel: Model<Product>,
    private readonly scheduleLockService: ScheduleLockService,
  ) { }

  async getProductByAdvertisementId(id: number): Promise<ProductResponse> {
    const product = await this.productModel.findOne({ advertisementId: id }).exec();

    if (!product) {
      throw new NotFoundException;
    }

    return this.mapProductModelToProductResponse(product);
  }

  async getAllSites(): Promise<SiteResponse[]> {
    const sites = await this.siteModel.find().exec();

    return this.mapSitesModelToDto(sites);
  }

  async createSite(site: SiteRequest): Promise<SiteResponse> {
    const createdSite = await new this.siteModel(site).save();

    return this.mapSiteModelToDto(createdSite);
  }

  async updateSite(id: string, site: SiteRequest): Promise<SiteResponse> {

    const updatedSite = await this.siteModel.findByIdAndUpdate(id, site, { new: true }).exec();

    if (!updatedSite) {
      throw new NotFoundException;
    }

    return this.mapSiteModelToDto(updatedSite);
  }

  async deleteSite(id: string): Promise<void> {
    await this.siteModel.deleteOne({ _id: id }).exec();
  }

  async addAdvertisement(advertisement: Advertisement): Promise<void> {
    await new this.productModel(advertisement).save();
  }

  async editAdvertisement(advertisement: Advertisement): Promise<void> {
    const result = await this.productModel.findOneAndUpdate({ advertisementId: advertisement.advertisementId }, advertisement).exec();
    if (!result) {
      await new this.productModel(advertisement).save();
    }
  }

  async deleteAdvertisement(advertisementId: number): Promise<void> {
    await this.productModel.deleteOne({ advertisementId }).exec();
  }

  async scrapeSite(siteId: string) {
    this.logger.log('Manual scraper started');

    const site = await this.siteModel.findById(siteId).exec();

    if (!site) {
      throw new NotFoundException("Site not found");
    }

    this.scrapeCategories(site.categoryIds).then(() => {
      this.logger.log('Manual scraper finished');
    });
  }

  @Cron('0 3 * * *')
  private async scrapePriceForAllAds() {
    if (!await this.scheduleLockService.lock('test', new Date(Date.now() + 1000 * 60 * 60 * 3))) {
      return;
    }

    this.logger.log('Scheduled scraper started');
    const categoryIds = await this.siteModel.distinct('categoryIds').exec() as number[];
    await this.scrapeCategories(categoryIds);
  
    this.logger.log('Scheduled scraper finished');
  }

  private async scrapeCategories(ids: number[]) {
    const browser = await puppeteer.use(StealthPlugin()).launch({
      headless: 'new',
      executablePath: executablePath(),
      args: ['--no-sandbox'],
    });
    await Promise.all(ids.map(async id => await this.scrapeCategory(id, browser)));
    await browser.close();
  }

  private async scrapeCategory(categoryId: number, browser: Browser) {
    this.logger.log(`Scrape category ${categoryId}`);
    const sites = await this.siteModel.find({ categoryIds: categoryId }).exec();
    const advertisements = await this.productModel.find({ categoryId }).exec();

    if (!sites.length || !advertisements?.length) {
      this.logger.log(`No sites or advertisements in category ${categoryId}`);
      return;
    }

    const updatedAdvertisements = await Promise.all(advertisements.map(async advertisement => {

      try {
        const results = await this.scrapeSites(sites, advertisement.title, browser);
        const bestResult = results.filter(r => r).reduce((prev, curr) => prev.price < curr.price ? prev : curr);

        advertisement.site = bestResult.site;
        advertisement.image = bestResult.image;
        advertisement.productTitle = bestResult.title;
        advertisement.url = bestResult.url;
        advertisement.price = bestResult.price;
      } catch(e) {
        this.logger.error(e);
        this.logger.log(`No results for ${advertisement.title} (${advertisement.id} - ${advertisement.advertisementId})`);

        advertisement.site = undefined;
        advertisement.image = undefined;
        advertisement.productTitle = undefined;
        advertisement.url = undefined;
        advertisement.price = undefined;
      }

      return advertisement;
    }));

    this.productModel.bulkSave(updatedAdvertisements);
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

  private mapProductModelToProductResponse(product: ProductDocument): ProductResponse {
    return {
      title: product.productTitle,
      image: product.image,
      price: product.price,
      site: product.site,
      url: product.url,
    };
  }

  private mapSitesModelToDto(sites: SiteDocument[]): SiteResponse[] {
    return sites.map(site => this.mapSiteModelToDto(site));
  }

  private mapSiteModelToDto(site: SiteDocument): SiteResponse {
    return {
      id: site._id.toHexString(),
      name: site.name,
      url: site.url,
      categoryIds: site.categoryIds,
      selector: site.selector,
    };
  }
}
