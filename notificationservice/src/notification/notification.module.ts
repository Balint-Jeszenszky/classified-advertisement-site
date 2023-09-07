import { Module } from '@nestjs/common';
import { MessageQueueController } from './message-queue.controller';
import { NotificationService } from './notification.service';
import { NotificationController } from './notification.controller';

@Module({
  controllers: [MessageQueueController, NotificationController],
  providers: [NotificationService]
})
export class NotificationModule {}
