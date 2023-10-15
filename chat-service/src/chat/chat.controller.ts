import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import Message from './entity/message.entity';
import { ChatService } from './chat.service';

export type MessageEvent = { message: Message, userId: number };

@Controller('chat')
export class ChatController {

  constructor(
    private readonly chatService: ChatService,
  ) { }

  @MessagePattern('message')
  getNotifications(@Payload() data: MessageEvent) {
    this.chatService.sendMessageToClient(data.userId, data.message);
  }
}
