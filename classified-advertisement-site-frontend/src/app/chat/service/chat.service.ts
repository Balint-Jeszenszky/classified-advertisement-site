import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Socket, io } from 'socket.io-client';
import { Message } from 'src/app/graphql/chat/generated';
import { LoggedInUserService } from 'src/app/service/logged-in-user.service';

type ChatMessage = Message & { chatId: number };

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private socket: Socket;
  private messageSubject: Subject<ChatMessage> = new Subject();
  private connecedComponents: Set<string> = new Set();

  constructor(
    private readonly loggedInUserService: LoggedInUserService,
  ) {
    this.socket = io('/chat', { autoConnect: false, path: '/api/chat/chat-ws' });
    this.socket.on('message', (data: ChatMessage) => {
      this.messageSubject.next(data);
    });
  }

  get message(): Observable<ChatMessage> {
    return this.messageSubject.asObservable();
  }

  connect(component: string) {
    this.connecedComponents.add(component);

    if (this.socket.disconnected && this.loggedInUserService.accessToken) {
      this.socket.io.opts.extraHeaders = { 'authorization': `Bearer ${this.loggedInUserService.accessToken}` }
      this.socket.connect();
    }
  }

  disconnect(component: string) {
    this.connecedComponents.delete(component);

    if (this.connecedComponents.size === 0) {
      this.socket.disconnect();
    }
  }

  sendMessage(message: string, chatId: number) {
    this.socket.send({ chatId, message });
  }
}
