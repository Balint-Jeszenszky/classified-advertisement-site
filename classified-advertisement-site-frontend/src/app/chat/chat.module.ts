import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatListComponent } from './chat-list/chat-list.component';
import { ChatComponent } from './chat/chat.component';
import { RouterModule, Routes } from '@angular/router';
import { ChatItemComponent } from './chat-list/chat-item/chat-item.component';

const routes: Routes = [
  { path: '', component: ChatListComponent },
  { path: ':id', component: ChatComponent },
  { path: 'advertisement/:advertisementId', component: ChatComponent },
];

@NgModule({
  declarations: [
    ChatListComponent,
    ChatComponent,
    ChatItemComponent
  ],
  imports: [
    RouterModule.forChild(routes),
    CommonModule
  ]
})
export class ChatModule { }
