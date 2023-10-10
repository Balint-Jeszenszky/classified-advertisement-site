import { Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ChatDetails } from '../chat-details.type';

@Component({
  selector: 'app-chat-item',
  templateUrl: './chat-item.component.html',
  styleUrls: ['./chat-item.component.scss']
})
export class ChatItemComponent {
  @Input() chat?: ChatDetails;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) { }

  openChat() {
    if (!this.chat) {
      return;
    }

    this.router.navigate(['conversation', this.chat.chat.id], { relativeTo: this.route });
  }
}
