import { Logger } from '@nestjs/common';
import { OnGatewayConnection, OnGatewayDisconnect, OnGatewayInit, SubscribeMessage, WebSocketGateway } from '@nestjs/websockets';
import { Socket, Server } from 'socket.io';
import { ChatService } from './chat.service';
import { parseAuthHeader } from 'src/auth/parse-auth-header';

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

    // this.service.addOnlineUser(user, client);
  }

  handleDisconnect(client: Socket) {
    this.logger.debug('disconnect');
  }

  @SubscribeMessage('message')
  handleMessage(client: Socket, message: string): string {
    this.logger.debug('message');
    return '';
  }
}
