import { Component, Input } from '@angular/core';
import { Chat } from 'src/app/graphql/chat/generated';

@Component({
  selector: 'app-chat-item',
  templateUrl: './chat-item.component.html',
  styleUrls: ['./chat-item.component.scss']
})
export class ChatItemComponent {
  @Input() chat?: Chat;
}
