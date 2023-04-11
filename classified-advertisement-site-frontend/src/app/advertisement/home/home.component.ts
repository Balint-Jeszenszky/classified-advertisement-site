import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdvertisementService, NewAdvertisementsResponse } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  advertisementGroups?: NewAdvertisementsResponse[];

  constructor(
    private readonly advertisementService: AdvertisementService,
    private readonly router: Router,
  ) { }

  ngOnInit(): void {
    this.advertisementService.getAdvertisementsNew().subscribe({
      next: res => this.advertisementGroups = res,
    });
  }

  onSearch(query: string) {
    this.router.navigate(['/search/', query]);
  }
}
