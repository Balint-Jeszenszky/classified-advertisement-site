import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoggedInUserService } from 'src/app/service/logged-in-user.service';

type Bid = {
  price: number,
}

@Injectable({
  providedIn: 'root'
})
export class LiveBidService {
  private socket?: WebSocket;

  constructor(
    private readonly loggedInUserService: LoggedInUserService,
  ) { }

  subscribeForBids(advertisementId: number): Observable<Bid> {
    const jwt = this.loggedInUserService.accessToken;
    if (jwt) {
      document.cookie = `jwt=${jwt}; path=/api/bid/live-bids; max-age=30`;
    }

    this.socket = new WebSocket(`${document.location.protocol === 'https:' ? 'wss' : 'ws'}://${document.location.host}/api/bid/live-bids`);

    this.socket.onopen = () => {
      this.socket?.send(JSON.stringify({
        type: 'subscribe',
        advertisementId,
      }));
    };

    return new Observable(subscriber => {
      if (!this.socket) {
        return;
      }

      this.socket.onmessage = event => {
        subscriber.next(JSON.parse(event.data));
      }
    });
  }

  bid(price: number) {
    this.socket?.send(JSON.stringify({
      type: 'bid',
      price,
    }));
  }

  unsubscribe() {
    this.socket?.close();
    this.socket = undefined;
  }
}
