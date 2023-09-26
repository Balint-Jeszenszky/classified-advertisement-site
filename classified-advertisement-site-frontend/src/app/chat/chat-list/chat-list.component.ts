import { Component, OnInit } from '@angular/core';
import { Apollo } from 'apollo-angular';
import { Chat, Query } from 'src/app/graphql/chat/generated';
import { GET_CHATS_FOR_USER } from 'src/app/graphql/chat/graphql.operations';

@Component({
  selector: 'app-chat-list',
  templateUrl: './chat-list.component.html',
  styleUrls: ['./chat-list.component.scss']
})
export class ChatListComponent implements OnInit {
  chats: Chat[] = [];

  constructor(
    private readonly apollo: Apollo,
  ) { }

  ngOnInit(): void {
    this.apollo.query<Query>({
      query: GET_CHATS_FOR_USER,
    }).subscribe(({data, error}) => {
      this.chats = data.chatsForUser;
    });
  }

}
