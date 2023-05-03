import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import request from 'supertest';
import { AppModule } from './../src/app.module';
import { ScraperService } from '../src/scraper/scraper.service';
import { MessageType } from '../src/scraper/dto/Advertisement.dto';
import { ScraperModule } from '../src/scraper/scraper.module';
import { getAdminAuthHeader, getUserAuthHeader, siteRequest } from './test.util';

describe('AppController (e2e)', () => {
  let app: INestApplication;
  let siteId: string;

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule, ScraperModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    app.useGlobalPipes(new ValidationPipe());
    await app.init();
    const scraperservice = app.get<ScraperService>(ScraperService);
    await scraperservice.addAdvertisement({ advertisementId: 5, categoryId: 3, title: 'test ad', type: MessageType.CREATE });
    siteId = (await scraperservice.createSite(siteRequest)).id;
  });

  afterEach(async () => {
    const scraperservice = app.get<ScraperService>(ScraperService);
    await scraperservice.deleteAdvertisement(5);
    await scraperservice.deleteSite(siteId);
  });

  it('/scraper/advertisement/{id} (GET)', () => {
    return request(app.getHttpServer())
      .get('/scraper/advertisement/5')
      .expect(200)
      .expect({});
  });

  it('/scraper/sites (GET) forbidden without auth header', () => {
    return request(app.getHttpServer())
      .get('/scraper/sites')
      .expect(401);
  });

  it('/scraper/sites (GET) forbidden with user auth header', () => {
    return request(app.getHttpServer())
      .get('/scraper/sites')
      .set('x-user-data', getUserAuthHeader())
      .expect(403);
  });

  it('/scraper/sites (GET) sends site list with admin auth header', () => {
    return request(app.getHttpServer())
      .get('/scraper/sites')
      .set('x-user-data', getAdminAuthHeader())
      .expect(200);
  });

  it('/scraper/site (POST) forbidden without auth header', () => {
    return request(app.getHttpServer())
      .post('/scraper/site')
      .send(siteRequest)
      .expect(401);
  });

  it('/scraper/site (POST) forbidden with user auth header', () => {
    return request(app.getHttpServer())
      .post('/scraper/site')
      .set('x-user-data', getUserAuthHeader())
      .send(siteRequest)
      .expect(403);
  });

  it('/scraper/site (POST) sends created site with admin auth header', () => {
    return request(app.getHttpServer())
      .post('/scraper/site')
      .set('x-user-data', getAdminAuthHeader())
      .send(siteRequest)
      .expect(201);
  });

  it('/scraper/site (POST) sends error for missing field', () => {
    const data = structuredClone(siteRequest);
    delete data.selector.image.property;
    return request(app.getHttpServer())
      .post('/scraper/site')
      .set('x-user-data', getAdminAuthHeader())
      .send(data)
      .expect(400);
  });

  it('/scraper/site/{id} (PUT) forbidden without auth header', () => {
    return request(app.getHttpServer())
      .put(`/scraper/site/${siteId}`)
      .send(siteRequest)
      .expect(401);
  });

  it('/scraper/site/{id} (PUT) forbidden with user auth header', () => {
    return request(app.getHttpServer())
      .put(`/scraper/site/${siteId}`)
      .set('x-user-data', getUserAuthHeader())
      .send(siteRequest)
      .expect(403);
  });

  it('/scraper/site/{id} (PUT) sends updated site with admin auth header', () => {
    return request(app.getHttpServer())
      .put(`/scraper/site/${siteId}`)
      .set('x-user-data', getAdminAuthHeader())
      .send(siteRequest)
      .expect(202);
  });

  it('/scraper/site/{id} (PUT) sends error for missing field', () => {
    const data = structuredClone(siteRequest);
    delete data.selector.image.property;
    return request(app.getHttpServer())
      .put(`/scraper/site/${siteId}`)
      .set('x-user-data', getAdminAuthHeader())
      .send(data)
      .expect(400);
  });

  it('/scraper/site/{id} (DELETE) forbidden with user auth header', () => {
    return request(app.getHttpServer())
      .delete(`/scraper/site/${siteId}`)
      .set('x-user-data', getUserAuthHeader())
      .expect(403);
  });

  it('/scraper/site/{id} (DELETE) successful with admin auth header', () => {
    return request(app.getHttpServer())
      .delete(`/scraper/site/${siteId}`)
      .set('x-user-data', getAdminAuthHeader())
      .expect(204);
  });
});
