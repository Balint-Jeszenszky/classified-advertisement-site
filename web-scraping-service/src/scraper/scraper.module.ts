import { Module } from '@nestjs/common';
import { ScraperService } from './scraper.service';
import { ScraperController } from './scraper.controller';
import { Site, SiteSchema } from './schemas/site.schema';
import { MongooseModule } from '@nestjs/mongoose';
import { Product, ProductSchema } from './schemas/product.schema';
import { MessageQueueController } from './message-queue.controller';

@Module({
  imports: [
    MongooseModule.forFeature([
      { name: Site.name, schema: SiteSchema },
      { name: Product.name, schema: ProductSchema },
    ]),
  ],
  providers: [ScraperService],
  controllers: [ScraperController, MessageQueueController],
})
export class ScraperModule {}
