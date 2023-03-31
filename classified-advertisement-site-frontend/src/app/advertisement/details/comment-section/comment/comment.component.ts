import { Component, Input } from '@angular/core';
import { CommentResponse } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrls: ['./comment.component.scss']
})
export class CommentComponent {
  @Input() comment?: CommentResponse;
}
