import { Module } from '@nestjs/common';
import { APP_GUARD } from '@nestjs/core';
import { HeaderAuthGuard } from './auth/header-auth.guard';
import { ScraperModule } from './scraper/scraper.module';

@Module({
  imports: [ScraperModule],
  controllers: [],
  providers: [
    {
      provide: APP_GUARD,
      useClass: HeaderAuthGuard,
    },
  ],
})
export class AppModule {}
