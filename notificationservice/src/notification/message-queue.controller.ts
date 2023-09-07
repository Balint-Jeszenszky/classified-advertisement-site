import { Controller } from '@nestjs/common';
import { Ctx, MessagePattern, Payload, RmqContext, Transport } from '@nestjs/microservices';

@Controller('message-queue')
export class MessageQueueController {
    
  @MessagePattern('email', Transport.RMQ)
  async processAdvertisementMessage(@Payload() content: any, @Ctx() context: RmqContext) {
    const channel = context.getChannelRef();
    const originalMsg = context.getMessage();

    // TODO

    channel.ack(originalMsg);
  }
}
