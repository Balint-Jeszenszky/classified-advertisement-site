import { AfterViewChecked, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Apollo } from 'apollo-angular';
import { Chat, Query, QueryChatArgs } from 'src/app/graphql/chat/generated';
import { GET_CHAT } from 'src/app/graphql/chat/graphql.operations';
import { LoggedInUserService } from 'src/app/service/logged-in-user.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit, AfterViewChecked {
  @ViewChild('messages') private messagesScrollContainer?: ElementRef;
  messageText: string = '';
  chat?: Chat;
  currentUserId?: number;

  constructor(
    private readonly apollo: Apollo,
    private readonly route: ActivatedRoute,
    private readonly loggedInUserService: LoggedInUserService,
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
  }
  
  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  sendMessage() {
    
  }

  scrollToBottom() {
    if (this.messagesScrollContainer)
      this.messagesScrollContainer.nativeElement.scrollTop = this.messagesScrollContainer.nativeElement.scrollHeight;            
  }
}
