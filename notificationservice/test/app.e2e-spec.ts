import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import * as request from 'supertest';
import { AppModule } from './../src/app.module';

describe('AppController (e2e)', () => {
  let app: INestApplication;

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    await app.init();
  });

  it('/notification/publicVapidKey (GET) should return vapid key', () => {
    return request(app.getHttpServer())
      .get('/notification/publicVapidKey')
      .set('x-user-data', getUserAuthHeader())
      .expect(200)
      .expect({
        publicVapidKey: process.env.VAPID_PUBLIC_KEY,
      });
  });

  it('/notification/publicVapidKey (GET) should return error without authenticatuon header', () => {
    return request(app.getHttpServer())
      .get('/notification/publicVapidKey')
      .expect(401);
  });

  it('/notification/pushSubscription (POST) should accept payload', () => {
    return request(app.getHttpServer())
      .post('/notification/pushSubscription')
      .set('x-user-data', getUserAuthHeader())
      .send({ endpoint: 'endpoint', keys: { p256dh: 'p256dh', auth: 'auth' }})
      .expect(201);
  });

  it('/notification/pushSubscription (POST) should return error without authenticatuon header', () => {
    return request(app.getHttpServer())
      .post('/notification/pushSubscription')
      .send({ endpoint: 'endpoint', keys: { p256dh: 'p256dh', auth: 'auth' }})
      .expect(401);
  });
});

function getUserAuthHeader() {
  return 'eyJ1c2VybmFtZSI6InVzZXIiLCJpZCI6MiwiZW1haWwiOiJ1c2VyQHVzZXIubG9jYWwiLCJyb2xlcyI6WyJST0xFX1VTRVIiXX0=';
}

function getAdminAuthHeader() {
  return 'eyJ1c2VybmFtZSI6ImFkbWluIiwiaWQiOjEsImVtYWlsIjoiYWRtaW5AYWRtaW4ubG9jYWwiLCJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl19';
}
