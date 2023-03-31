import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AdvertisementResponse, AdvertisementService } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.scss']
})
export class DetailsComponent implements OnInit {
  id?: number;
  advertisement?: AdvertisementResponse;

  constructor(
    private route: ActivatedRoute,
    private advertisementService: AdvertisementService,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = +params['id'];
      this.advertisementService.getAdvertisementId(this.id).subscribe(res => {
        this.advertisement = res;
      });
    });
  }
}
