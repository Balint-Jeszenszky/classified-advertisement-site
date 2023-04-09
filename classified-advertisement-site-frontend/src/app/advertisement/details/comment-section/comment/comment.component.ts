import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommentResponse } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss']
})
export class CommentComponent {
  @Input() comment?: CommentResponse;
  @Input() deletable: boolean = false;
  @Output() deleteEvent: EventEmitter<number> = new EventEmitter();

  onDelete() {
    if (this.comment) {
      this.deleteEvent.emit(this.comment.id);
    }
  }
}
