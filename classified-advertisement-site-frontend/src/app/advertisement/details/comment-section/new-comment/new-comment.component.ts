import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommentResponse, CommentService } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-new-comment',
  templateUrl: './new-comment.component.html',
  styleUrls: ['./new-comment.component.scss']
})
export class NewCommentComponent {
  @Input() advertisementId?: number;
  @Output() newCommentEvent: EventEmitter<CommentResponse> = new EventEmitter();
  content: string = '';
  disabled: boolean = false;

  constructor(
    private readonly commentService: CommentService,
  ) { }

  onComment() {
    if (!this.advertisementId) {
      return;
    }

    this.disabled = true;

    this.commentService.postAdvertisementIdComment(
      this.advertisementId,
      { content: this.content },
    ).subscribe(res => {
      this.content = '';
      this.disabled = false;
      this.newCommentEvent.emit(res);
    });
  }
}
