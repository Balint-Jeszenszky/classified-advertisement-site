import { Component, Input, OnInit } from '@angular/core';
import { CommentResponse, CommentService } from 'src/app/openapi/advertisementservice';
import { UserDetailsResponse } from 'src/app/openapi/gateway/model/userDetailsResponse';
import { PublicUserDetailsResponse, PublicUserService } from 'src/app/openapi/userservice';
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
  user?: UserDetailsResponse;
  admin: boolean = false;
  users?: PublicUserDetailsResponse[];

  constructor(
    private readonly commentService: CommentService,
    private readonly loggedInUserService: LoggedInUserService,
    private readonly publicUserService: PublicUserService,
  ) { }

  ngOnInit(): void {
    if (this.advertisementId) {
      this.commentService.getAdvertisementIdComment(this.advertisementId).subscribe(comments => {
        this.comments = comments;
        this.publicUserService.getUserId([...new Set(comments.map(c => c.creatorId))]).subscribe({
          next: users => this.users = users,
        });
      });
    }
    this.loggedInUserService.user.subscribe(user => {
      this.user = user;
      this.admin = !!user?.roles.includes(Role.ROLE_ADMIN);
    });
  }

  addNewComment(comment: CommentResponse) {
    if (this.user && this.users?.findIndex(u => u.id === this.user?.id) === -1) {
      this.users.push({ id: this.user.id, username: this.user.username });
    }
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
