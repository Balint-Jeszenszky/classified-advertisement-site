import { Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Chat } from 'src/app/graphql/chat/generated';

@Component({
  selector: 'app-chat-item',
  templateUrl: './chat-item.component.html',
  styleUrls: ['./chat-item.component.scss']
})
export class ChatItemComponent {
  @Input() chat?: Chat;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) { }

  openChat() {
    if (!this.chat) {
      return;
    }

    this.router.navigate(['conversation', this.chat.id], { relativeTo: this.route });
  }
}
