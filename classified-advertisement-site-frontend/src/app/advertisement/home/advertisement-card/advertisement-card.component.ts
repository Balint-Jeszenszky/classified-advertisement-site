import { Component, Input } from '@angular/core';
import { AdvertisementResponse } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-advertisement-card',
  templateUrl: './advertisement-card.component.html',
  styleUrls: ['./advertisement-card.component.scss']
})
export class AdvertisementCardComponent {
  @Input() advertisement?: AdvertisementResponse;
}
