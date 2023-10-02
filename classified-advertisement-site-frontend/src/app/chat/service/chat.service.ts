import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Socket, io } from 'socket.io-client';
import { Message } from 'src/app/graphql/chat/generated';
import { LoggedInUserService } from 'src/app/service/logged-in-user.service';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private socket: Socket;
  private messageSubject: Subject<Message> = new Subject();
  private connecedComponents: Set<string> = new Set();

  constructor(
    private readonly loggedInUserService: LoggedInUserService,
  ) {
    this.socket = io('/chat', { autoConnect: false, path: '/api/chat/chat-ws' });
    this.socket.on('message', (data: Message) => {
      this.messageSubject.next(data);
    });
  }

  get message(): Observable<Message> {
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
}
