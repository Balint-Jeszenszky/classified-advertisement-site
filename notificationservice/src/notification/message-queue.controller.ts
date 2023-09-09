import { Controller } from '@nestjs/common';
import { Ctx, MessagePattern, Payload, RmqContext, Transport } from '@nestjs/microservices';
import { Email, Push } from './dto/Notification.dto';
import { NotificationService } from './notification.service';

@Controller('message-queue')
export class MessageQueueController {

  constructor(
    private readonly notificationService: NotificationService,
  ) { }
    
  @MessagePattern('email', Transport.RMQ)
  async processEmailMessage(@Payload() email: Email, @Ctx() context: RmqContext) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    try {
      await this.notificationService.sendEmail(email);
      channel.ack(originalMsg);
    } catch {
      channel.nack(originalMsg);
    }
  }
    
  @MessagePattern('push', Transport.RMQ)
  async processPushMessage(@Payload() push: Push, @Ctx() context: RmqContext) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    try {
      await this.notificationService.sendWebPushNotification(push);
      channel.ack(originalMsg);
    } catch {
      channel.nack(originalMsg);
    }
  }
}
