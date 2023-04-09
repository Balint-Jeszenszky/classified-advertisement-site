import { Component, Input, OnInit } from '@angular/core';
import { CommentResponse, CommentService } from 'src/app/openapi/advertisementservice';
import { LoggedInUserService } from 'src/app/service/logged-in-user.service';
import { Role } from 'src/app/service/types';

@Component({
  selector: 'app-comment-section',
  templateUrl: './comment-section.component.html',
  styleUrls: ['./comment-section.component.scss']
})
export class CommentSectionComponent implements OnInit {
  @Input() advertisementId?: number;
  comments: CommentResponse[] = [];
  userId?: number;
  admin: boolean = false;

  constructor(
    private readonly commentService: CommentService,
    private readonly loggedInUserService: LoggedInUserService
  ) { }

  ngOnInit(): void {
    if (this.advertisementId) {
      this.commentService.getAdvertisementIdComment(this.advertisementId).subscribe(res => {
        this.comments = res;
      });
    }
    this.loggedInUserService.user.subscribe(user => {
      this.userId = user?.id;
      this.admin = !!user?.roles.includes(Role.ROLE_ADMIN);
    });
  }

  addNewComment(comment: CommentResponse) {
    this.comments.push(comment);
  }

  deleteComment(id: number) {
    if (confirm('Delete comment?')) {
      this.commentService.deleteCommentId(id).subscribe({
        next: () => this.comments = this.comments.filter(c => c.id !== id),
      });
    }
  }
}
