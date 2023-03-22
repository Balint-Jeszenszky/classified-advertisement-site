import { Component } from '@angular/core';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.scss']
})
export class AuthenticationComponent {
  private username: Subject<string> = new Subject<string>();
  selectedTabIndex: number = 0;

  showLogin(username: string) {
    this.selectedTabIndex = 0;
    this.username.next(username);
  }

  get usernameAsObservable() {
    return this.username.asObservable();
  }

}
