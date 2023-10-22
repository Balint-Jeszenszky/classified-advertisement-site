import { AfterViewChecked, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Apollo } from 'apollo-angular';
import { Chat, Mutation, MutationSendMessageForAdvertisementArgs, Query, QueryChatArgs, QueryChatIdByAdvertisementArgs } from 'src/app/graphql/chat/generated';
import { GET_CHAT, GET_CHAT_BY_ADVERTISEMENT, SEND_MESSAGE_FOR_ADVERTISEMENT } from 'src/app/graphql/chat/graphql.operations';
import { LoggedInUserService } from 'src/app/service/logged-in-user.service';
import { ChatService } from '../service/chat.service';
import { AdvertisementService } from 'src/app/openapi/advertisementservice';
import { PublicUserService } from 'src/app/openapi/userservice';

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
  fromUserId?: number; 
  advertisementId?: number;
  fromUsername: string = '';
  advertisementTitle: string = '';

  constructor(
    private readonly apollo: Apollo,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly loggedInUserService: LoggedInUserService,
    private readonly chatService: ChatService,
    private readonly advertisementService: AdvertisementService,
    private readonly publicUserService: PublicUserService,
  ) { }

  ngOnInit(): void {
    this.loggedInUserService.user.subscribe(user => {
      this.currentUserId = user?.id;
    });

    this.route.params.subscribe(params => {
      if (!params['id'] && !params['advertisementId']) {
        return;
      }

      const id = +params['id'];
      this.advertisementId = +params['advertisementId'];

      if (id) {
        this.apollo.query<Query, QueryChatArgs>({
          query: GET_CHAT,
          variables: {
            id,
          },
        }).subscribe(({data, error}) => {
          if (data.chat) {
            this.chat = data.chat;
            this.advertisementId = data.chat.advertisementId;
            this.fromUserId = data.chat.fromUserId === this.currentUserId ? data.chat.advertisementOwnerUserId : this.chat.fromUserId;
            this.advertisementService.getAdvertisementId(data.chat.advertisementId).subscribe(res => {
              this.advertisementTitle = res.title;
            });
            this.publicUserService.getUserId([this.fromUserId]).subscribe(res => {
              this.fromUsername = res[0].username;
            });
            this.scrollToBottom();
          }
        });
      } else if (this.advertisementId) {
        this.navigateToConversation(this.advertisementId).catch(() => {
          if (!this.advertisementId) {
            return;
          }

          this.advertisementService.getAdvertisementId(this.advertisementId).subscribe(res => {
            this.advertisementTitle = res.title;
            this.publicUserService.getUserId([res.advertiserId]).subscribe(users => {
              this.fromUsername = users[0].username;
            });
          });
        });
      }
    });

    this.chatService.connect(CHAT_COMPONENT);
    this.chatService.message.subscribe(message => {
      if (message.chat.id === this.chat?.id) {
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
      if (this.advertisementId) {
        const advertisementId = this.advertisementId;

        this.apollo.mutate<Mutation, MutationSendMessageForAdvertisementArgs>({
          mutation: SEND_MESSAGE_FOR_ADVERTISEMENT,
          variables: {
            newMessage: {
              advertisementId,
              text: this.messageText,
            },
          },
        }).subscribe(() => {
          this.navigateToConversation(advertisementId);
        });
      }
      return;
    }

    this.chatService.sendMessage(this.messageText, this.chat.id);

    this.messageText = '';
  }

  
  scrollToBottom() {
    if (this.messagesScrollContainer)
    this.messagesScrollContainer.nativeElement.scrollTop = this.messagesScrollContainer.nativeElement.scrollHeight;            
  }

  private navigateToConversation(advertisementId: number): Promise<void> {
    return new Promise((resolve, reject) => {
      this.apollo.query<Query, QueryChatIdByAdvertisementArgs>({
        query: GET_CHAT_BY_ADVERTISEMENT,
        variables: {
          advertisementId,
        },
      }).subscribe({
        next: ({data}) => {
          resolve();
          if (data.chatIdByAdvertisement) {
            this.router.navigate(['/chat/conversation/', data.chatIdByAdvertisement.id], { skipLocationChange: true });
          }
        },
        error: () => {
          reject();
        }
      });
    });
  }
}
