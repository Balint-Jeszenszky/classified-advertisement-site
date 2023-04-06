import { Component, Input } from '@angular/core';
import { AdvertisementResponse } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-advertisement-list-item',
  templateUrl: './advertisement-list-item.component.html',
  styleUrls: ['./advertisement-list-item.component.scss']
})
export class AdvertisementListItemComponent {
  @Input() advertisement?: AdvertisementResponse;
}
