import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdvertisementResponse, AdvertisementService, NewAdvertisementsResponse } from 'src/app/openapi/advertisementservice';
import { BidService } from 'src/app/openapi/bidservice';
import { LoadingState } from 'src/app/shared/components/spinner/spinner.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  advertisementGroups?: NewAdvertisementsResponse[];
  loadingState: LoadingState = LoadingState.LOADING;

  constructor(
    private readonly advertisementService: AdvertisementService,
    private readonly router: Router,
    private readonly bidService: BidService,
  ) { }

  ngOnInit(): void {
    this.advertisementService.getAdvertisementsNew().subscribe({
      next: res => {
        this.loadingState = LoadingState.LOADED;
        this.advertisementGroups = res;
        this.loadBids();
      },
      error: () => {
        this.loadingState = LoadingState.ERROR;
      },
    });
  }

  onSearch(query: string) {
    this.router.navigate(['/search/', query]);
  }

  loadBids() {
    if (!this.advertisementGroups) {
      return;
    }

    const ids = this.advertisementGroups.flatMap(g => g.advertisements).filter(a => a.type === AdvertisementResponse.TypeEnum.Bid).map(a => a.id);

    if (!ids.length) {
      return;
    }

    this.bidService.getCurrentBidsIds(ids).subscribe(res => {
      res.forEach(bid => {
        const advertisement = this.advertisementGroups?.flatMap(g => g.advertisements).find(a => a.id === bid.id);
        if (advertisement) {
          advertisement.price = bid.price;
        }
      });
    });
  }
}
