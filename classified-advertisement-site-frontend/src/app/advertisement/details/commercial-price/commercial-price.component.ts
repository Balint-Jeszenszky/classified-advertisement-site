import { Component, Input } from '@angular/core';
import { ProductResponse } from 'src/app/openapi/webscraperservice';

@Component({
  selector: 'app-commercial-price',
  templateUrl: './commercial-price.component.html',
  styleUrls: ['./commercial-price.component.scss']
})
export class CommercialPriceComponent {
  @Input() product?: ProductResponse;
  @Input() advertisementPrice?: number;
}
