import { Body, Controller, Delete, Get, HttpCode, Param, Post, Put } from '@nestjs/common';
import { ScraperService } from './scraper.service';
import { ProductResponse } from './dto/ProductResponse.dto';
import { Public } from 'src/auth/public.decorator';
import { SiteRequest } from './dto/SiteRequest.dto';
import { SiteResponse } from './dto/SiteResponse.dto';
import { HasRole } from 'src/auth/hasrole.decorator';
import { Role } from 'src/auth/role.enum';
import { ApiAcceptedResponse, ApiCreatedResponse, ApiNotFoundResponse, ApiOkResponse, ApiTags } from '@nestjs/swagger';

@ApiTags('scraper')
@Controller('scraper')
export class ScraperController {

  constructor(
    private readonly scraperService: ScraperService,
  ) { }

  @Public()
  @Get('advertisement/:id')
  @ApiOkResponse({ type: ProductResponse })
  @ApiNotFoundResponse()
  getPriceByAdvertisementId(@Param('id') id: number): Promise<ProductResponse> {
    return this.scraperService.getPriceByAdvertisementId(id);
  }

  @Get('sites')
  @HasRole(Role.ADMIN)
  @ApiOkResponse({ type: SiteResponse, isArray: true })
  getAllSites(): Promise<SiteResponse[]> {
    return this.scraperService.getAllSites();
  }

  @Post('site')
  @HasRole(Role.ADMIN)
  @ApiCreatedResponse({ type: SiteResponse })
  createSite(@Body() site: SiteRequest): Promise<SiteResponse> {
    return this.scraperService.createSite(site);
  }

  @Put('site/:id')
  @HasRole(Role.ADMIN)
  @HttpCode(202)
  @ApiAcceptedResponse({ type: SiteResponse })
  @ApiNotFoundResponse()
  updateSite(@Param('id') id: string, @Body() site: SiteRequest): Promise<SiteResponse> {
    return this.scraperService.updateSite(id, site);
  }

  @Delete('site/:id')
  @HasRole(Role.ADMIN)
  @HttpCode(204)
  deleteSite(@Param('id') id: string): Promise<void> {
    return this.scraperService.deleteSite(id);
  }
}
