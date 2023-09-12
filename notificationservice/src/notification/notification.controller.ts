import { Body, Controller, Get, Post, Request } from '@nestjs/common';
import { NotificationService } from './notification.service';
import { ApiCreatedResponse, ApiOkResponse, ApiSecurity, ApiTags } from '@nestjs/swagger';
import { PublicVapidKeyResponse } from './dto/PublicVapidKeyResponse.dto';
import { PushSubscriptionRequest } from './dto/PushSubscriptionRequest.dto';

@ApiTags('notifications')
@ApiSecurity('JWT')
@Controller('notification')
export class NotificationController {

  constructor(
    private readonly notificationService: NotificationService,
  ) { }

  @Get('publicVapidKey')
  @ApiOkResponse({ type: PublicVapidKeyResponse })
  getPublicVapidKey(): PublicVapidKeyResponse {
    return new PublicVapidKeyResponse(this.notificationService.getPublicVapidKey());
  }

  @Post('pushSubscription')
  @ApiCreatedResponse()
  pushSubscription(@Request() req, @Body() subscription: PushSubscriptionRequest): void {
    this.notificationService.subscribePushNotification(req.user, subscription);
  }
}
