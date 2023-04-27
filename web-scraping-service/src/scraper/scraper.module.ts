import { Module } from '@nestjs/common';
import { ScraperService } from './scraper.service';
import { ScraperController } from './scraper.controller';
import { Site, SiteSchema } from './schemas/site.schema';
import { MongooseModule } from '@nestjs/mongoose';
import { Price, PriceSchema } from './schemas/price.schema';
import { MessageQueueController } from './message-queue.controller';

@Module({
  imports: [
    MongooseModule.forFeature([
      { name: Site.name, schema: SiteSchema },
      { name: Price.name, schema: PriceSchema },
    ]),
  ],
  providers: [ScraperService],
  controllers: [ScraperController, MessageQueueController],
})
export class ScraperModule {}
