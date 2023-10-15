import { Logger } from '@nestjs/common';
import { OnGatewayConnection, OnGatewayDisconnect, OnGatewayInit, SubscribeMessage, WebSocketGateway } from '@nestjs/websockets';
import { Socket, Server } from 'socket.io';
import { ChatService } from './chat.service';
import { parseAuthHeader } from 'src/auth/parse-auth-header';

type MessageRequest = {
  chatId: number,
  message: string,
};

@WebSocketGateway({ namespace: 'chat', path: '/api/chat-ws' })
export class ChatGateway implements OnGatewayInit, OnGatewayConnection, OnGatewayDisconnect {
  private readonly logger = new Logger(ChatGateway.name);
  private server: Server;

  constructor(
    private readonly chatService: ChatService,
  ) { }

  afterInit(server: Server) {
    this.server = server;
  }

  handleConnection(client: Socket, ...args: any[]) {
    try {
      client.data.user = parseAuthHeader(client.handshake.headers['x-user-data'] as string);
    } catch {
      client.disconnect(true);
      return;
    }

    this.chatService.addOnlineUser(client);
  }

  handleDisconnect(client: Socket) {
    this.chatService.removeOnlineUser(client);
  }

  @SubscribeMessage('message')
  async handleMessage(client: Socket, message: MessageRequest) {
    const savedMessage = await this.chatService.sendMessageToChat(message.chatId, client.data.user.id, message.message);
    client.emit('message', savedMessage );
  }
}
