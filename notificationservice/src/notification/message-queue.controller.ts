import { Controller, Logger } from '@nestjs/common';
import { Ctx, MessagePattern, Payload, RmqContext, Transport } from '@nestjs/microservices';
import { Email, Push } from './dto/Notification.dto';
import { NotificationService } from './notification.service';

@Controller('message-queue')
export class MessageQueueController {
  private readonly logger: Logger = new Logger(MessageQueueController.name);

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
    } catch (err) {
      this.logger.error(err);
      setTimeout(() => channel.nack(originalMsg), 10000);
    }
  }
    
  @MessagePattern('push', Transport.RMQ)
  async processPushMessage(@Payload() push: Push, @Ctx() context: RmqContext) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    try {
      await this.notificationService.sendWebPushNotification(push);
      channel.ack(originalMsg);
    } catch (err) {
      this.logger.error(err);
      channel.nack(originalMsg);
    }
  }
}
