import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthenticationService } from 'src/app/openapi/userservice';

@Component({
  selector: 'app-confirm-email',
  templateUrl: './confirm-email.component.html',
  styleUrls: ['./confirm-email.component.scss']
})
export class ConfirmEmailComponent implements OnInit {
  success: boolean = false;
  error?: string;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly authenticationService: AuthenticationService,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const key = params['key'];
      this.authenticationService.postAuthVerifyEmail({ key }).subscribe({
        next: () => this.success = true,
        error: err => this.error = err.error,
      });
    });
  }
}
