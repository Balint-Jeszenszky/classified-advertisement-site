import { Module } from '@nestjs/common';
import { ChatService } from './chat.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ChatResolver } from './chat.resolver';
import { ChatGateway } from './chat.gateway';
import Chat from './entity/chat.entity';
import Message from './entity/message.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Chat, Message])],
  providers: [ChatService, ChatResolver, ChatGateway]
})
export class ChatModule {}
