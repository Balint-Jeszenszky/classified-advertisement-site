import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommentResponse } from 'src/app/openapi/advertisementservice';
import { PublicUserDetailsResponse } from 'src/app/openapi/userservice';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss']
})
export class CommentComponent {
  @Input() comment?: CommentResponse;
  @Input() deletable: boolean = false;
  @Input() users?: PublicUserDetailsResponse[];
  @Output() deleteEvent: EventEmitter<number> = new EventEmitter();

  getUsername(id: number) {
    return this.users?.find(u => u.id === id)?.username;
  }

  onDelete() {
    if (this.comment) {
      this.deleteEvent.emit(this.comment.id);
    }
  }
}
