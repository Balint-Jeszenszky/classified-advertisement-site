import { Module } from '@nestjs/common';
import { ScraperService } from './scraper.service';
import { ScraperController } from './scraper.controller';
import { Site, SiteSchema } from './schemas/site.schema';
import { MongooseModule } from '@nestjs/mongoose';

@Module({
  imports: [MongooseModule.forFeature([{ name: Site.name, schema: SiteSchema }])],
  providers: [ScraperService],
  controllers: [ScraperController],
})
export class ScraperModule {}
