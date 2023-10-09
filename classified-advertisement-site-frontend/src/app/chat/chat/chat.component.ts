import { AfterViewChecked, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Apollo } from 'apollo-angular';
import { Chat, Query, QueryChatArgs } from 'src/app/graphql/chat/generated';
import { GET_CHAT } from 'src/app/graphql/chat/graphql.operations';
import { LoggedInUserService } from 'src/app/service/logged-in-user.service';
import { ChatService } from '../service/chat.service';

const CHAT_COMPONENT = 'CHAT_COMPONENT';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit, AfterViewChecked, OnDestroy { 
  @ViewChild('messages') private messagesScrollContainer?: ElementRef;
  messageText: string = '';
  chat?: Chat;
  currentUserId?: number;

  constructor(
    private readonly apollo: Apollo,
    private readonly route: ActivatedRoute,
    private readonly loggedInUserService: LoggedInUserService,
    private readonly chatService: ChatService,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (!params['id']) {
        return;
      }

      const id = +params['id'];

      this.apollo.query<Query, QueryChatArgs>({
        query: GET_CHAT,
        variables: {
          id,
        },
      }).subscribe(({data, error}) => {
        if (data.chat) {
          this.chat = data.chat;
          this.scrollToBottom();
        }
      });
    });

    this.loggedInUserService.user.subscribe(user => {
      this.currentUserId = user?.id;
    });

    this.chatService.connect(CHAT_COMPONENT);
    this.chatService.message.subscribe(message => {
      if (message.chatId === this.chat?.id) {
        this.chat?.messages.push(message);
      }
    });
  }
  
  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  ngOnDestroy(): void {
    this.chatService.disconnect(CHAT_COMPONENT);
  }

  sendMessage() {
    if (!this.chat) {
      return;
    }

    this.chatService.sendMessage(this.messageText, this.chat.id);

    this.messageText = '';
  }

  scrollToBottom() {
    if (this.messagesScrollContainer)
      this.messagesScrollContainer.nativeElement.scrollTop = this.messagesScrollContainer.nativeElement.scrollHeight;            
  }
}
