import { Body, Controller, Delete, Get, Param, Post, Put } from '@nestjs/common';
import { ScraperService } from './scraper.service';
import { ProductResponse } from './dto/ProductResponse.dto';
import { Public } from 'src/auth/public.decorator';
import { SiteRequest } from './dto/SiteRequest.dto';
import { SiteResponse } from './dto/SiteResponse.dto';
import { HasRole } from 'src/auth/hasrole.decorator';
import { Role } from 'src/auth/role.enum';

@Controller('scraper')
export class ScraperController {

  constructor(
    private readonly scraperService: ScraperService,
  ) { }

  @Public()
  @Get('advertisement/:id')
  getPriceByAdvertisementId(@Param('id') id: number): ProductResponse {
    return this.scraperService.getPriceByAdvertisementId(id);
  }

  @Get('sites')
  @HasRole(Role.ADMIN)
  getAllSites(): SiteResponse[] {
    return this.scraperService.getAllSites();
  }

  @Post('site')
  @HasRole(Role.ADMIN)
  createSite(@Body() site: SiteRequest): SiteResponse {
    return this.scraperService.createSite(site);
  }

  @Put('site/:id')
  @HasRole(Role.ADMIN)
  updateSite(@Param('id')id: string, @Body() site: SiteRequest): SiteResponse {
    return this.scraperService.updateSite(id, site);
  }

  @Delete('site/:id')
  @HasRole(Role.ADMIN)
  deleteSite(@Param('id')id: string) {
    this.scraperService.deleteSite(id);
  }
}
