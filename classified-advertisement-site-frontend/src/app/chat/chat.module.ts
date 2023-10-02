import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatListComponent } from './chat-list/chat-list.component';
import { ChatComponent } from './chat/chat.component';
import { RouterModule, Routes } from '@angular/router';
import { ChatItemComponent } from './chat-list/chat-item/chat-item.component';

import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { SharedModule } from '../shared/shared.module';
import { FormsModule } from '@angular/forms';
import { MessageComponent } from './chat/message/message.component';

const routes: Routes = [
  { path: '', component: ChatListComponent },
  { path: 'conversation/:id', component: ChatComponent },
  { path: 'advertisement/:advertisementId', component: ChatComponent },
];

@NgModule({
  declarations: [
    ChatListComponent,
    ChatComponent,
    ChatItemComponent,
    MessageComponent
  ],
  imports: [
    RouterModule.forChild(routes),
    CommonModule,
    SharedModule,
    FormsModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
  ]
})
export class ChatModule { }
