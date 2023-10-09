import { Module } from '@nestjs/common';
import { ChatService } from './chat.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ChatResolver } from './chat.resolver';
import { ChatGateway } from './chat.gateway';
import Chat from './entity/chat.entity';
import Message from './entity/message.entity';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { ChatController } from './chat.controller';

@Module({
  imports: [
    TypeOrmModule.forFeature([Chat, Message]),
    ClientsModule.register([
      {
        name: 'REALTIME_CHAT_SERVICE',
        transport: Transport.REDIS,
      },
    ]),
  ],
  providers: [ChatService, ChatResolver, ChatGateway],
  controllers: [ChatController]
})
export class ChatModule {}
