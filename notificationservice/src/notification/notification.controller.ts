import { Controller, Get } from '@nestjs/common';
import { NotificationService } from './notification.service';

@Controller('notification')
export class NotificationController {

  constructor(
    private readonly notificationService: NotificationService,
  ) { }

  @Get('publicVapidKey')
  getPublicVapidKey() {
    return { publicVapidKey: this.notificationService.getPublicVapidKey() };
  }
}