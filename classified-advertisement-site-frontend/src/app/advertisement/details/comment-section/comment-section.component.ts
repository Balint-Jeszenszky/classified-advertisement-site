import { Component, Input, OnInit } from '@angular/core';
import { CommentResponse, CommentService } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-comment-section',
  templateUrl: './comment-section.component.html',
  styleUrls: ['./comment-section.component.scss']
})
export class CommentSectionComponent implements OnInit {
  @Input() advertisementId?: number;
  comments: CommentResponse[] = [];

  constructor(
    private readonly commentService: CommentService,
  ) { }

  ngOnInit(): void {
    if (this.advertisementId) {
      this.commentService.getAdvertisementIdComment(this.advertisementId).subscribe(res => {
        this.comments = res;
      });
    }
  }

  addNewComment(comment: CommentResponse) {
    this.comments.push(comment);
  }
}
