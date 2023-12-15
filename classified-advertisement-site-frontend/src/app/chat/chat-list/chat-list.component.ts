import { Component, OnInit } from '@angular/core';
import { Apollo } from 'apollo-angular';
import { Query } from 'src/app/graphql/chat/generated';
import { GET_CHATS_FOR_USER } from 'src/app/graphql/chat/graphql.operations';
import { AdvertisementService } from 'src/app/openapi/advertisementservice';
import { PublicUserService } from 'src/app/openapi/userservice';
import { LoggedInUserService } from 'src/app/service/logged-in-user.service';
import { ChatDetails } from './chat-details.type';
import { LoadingState } from 'src/app/shared/components/spinner/spinner.component';

@Component({
  selector: 'app-chat-list',
  templateUrl: './chat-list.component.html',
  styleUrls: ['./chat-list.component.scss']
})
export class ChatListComponent implements OnInit {
  chats?: ChatDetails[];
  loadingState: LoadingState = LoadingState.LOADING;

  constructor(
    private readonly apollo: Apollo,
    private readonly loggedInUserService: LoggedInUserService,
    private readonly advertisementService: AdvertisementService,
    private readonly publicUserService: PublicUserService,
  ) { }

  ngOnInit(): void {
    this.loggedInUserService.user.subscribe(user => {
      this.apollo.query<Query>({
        query: GET_CHATS_FOR_USER,
      }).subscribe({
        next: ({data}) => {
          this.loadingState = LoadingState.LOADED;
          this.chats = data.chatsForUser.map(chat => ({ chat }));
          if (this.chats.length) {
            this.advertisementService.getAdvertisementsListIds(data.chatsForUser.map(c => c.advertisementId)).subscribe(res => {
              this.chats?.forEach(c => {
                c.advertisementTitle = res.find(a => a.id === c.chat.advertisementId)?.title;
              });
            });
            this.publicUserService.getUserId(data.chatsForUser.map(c => c.fromUserId === user?.id ? c.advertisementOwnerUserId : c.fromUserId)).subscribe(res => {
              this.chats?.forEach(c => {
                c.fromUsername = res.find(u => u.id === c.chat.fromUserId || c.chat.advertisementOwnerUserId)?.username;
              });
            });
          }
        },
        error: () => {
          this.loadingState = LoadingState.ERROR;
        },
      });
    });
  }
}
