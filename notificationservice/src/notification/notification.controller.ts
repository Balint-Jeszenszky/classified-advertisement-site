import { Controller, Get } from '@nestjs/common';
import { NotificationService } from './notification.service';
import { ApiOkResponse, ApiSecurity, ApiTags } from '@nestjs/swagger';
import { PublicVapidKeyResponse } from './dto/PublicVapidKeyResponse.dto';

@ApiTags('notifications')
@ApiSecurity('JWT')
@Controller('notification')
export class NotificationController {

  constructor(
    private readonly notificationService: NotificationService,
  ) { }

  @Get('publicVapidKey')
  @ApiOkResponse({ type: PublicVapidKeyResponse })
  getPublicVapidKey() {
    return new PublicVapidKeyResponse(this.notificationService.getPublicVapidKey());
  }
}
