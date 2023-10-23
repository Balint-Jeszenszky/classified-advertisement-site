import { Module } from '@nestjs/common';
import { APP_GUARD } from '@nestjs/core';
import { HeaderAuthGuard } from './auth/header-auth.guard';
import { ScraperModule } from './scraper/scraper.module';
import { RolesGuard } from './auth/role.guard';
import { MongooseModule } from '@nestjs/mongoose';
import { ScheduleModule } from '@nestjs/schedule';
import { ConfigModule } from '@nestjs/config';
import { ScheduleLockModule } from './schedule-lock/schedule-lock.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      envFilePath: process.env.NODE_ENV ? `.${process.env.NODE_ENV}.env` : '.env',
      isGlobal: true,
    }),
    MongooseModule.forRoot(
      `mongodb://${process.env.MONGO_URL}`,
      {
        authSource: 'admin',
        user: process.env.MONGO_USER,
        pass: process.env.MONGO_PASS,
      },
    ),
    ScraperModule,
    ScheduleModule.forRoot(),
    ScheduleLockModule,
  ],
  controllers: [],
  providers: [
    {
      provide: APP_GUARD,
      useClass: HeaderAuthGuard,
    },
    {
      provide: APP_GUARD,
      useClass: RolesGuard,
    },
  ],
})
export class AppModule {}
