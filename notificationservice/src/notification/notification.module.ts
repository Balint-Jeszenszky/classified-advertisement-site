import { Module } from '@nestjs/common';
import { MessageQueueController } from './message-queue.controller';
import { NotificationService } from './notification.service';

@Module({
  controllers: [MessageQueueController],
  providers: [NotificationService]
})
export class NotificationModule {}
