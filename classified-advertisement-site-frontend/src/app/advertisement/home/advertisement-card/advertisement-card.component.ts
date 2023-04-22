import { Component, Input } from '@angular/core';
import { AdvertisementResponse } from 'src/app/openapi/advertisementservice';
import { ImagesService } from 'src/app/openapi/imageprocessingservice';

@Component({
  selector: 'app-advertisement-card',
  templateUrl: './advertisement-card.component.html',
  styleUrls: ['./advertisement-card.component.scss']
})
export class AdvertisementCardComponent {
  @Input() advertisement?: AdvertisementResponse;
  thumbnail?: Blob;

  constructor(
    private readonly imagesService: ImagesService,
  ) { }

  ngOnInit(): void {
    if (this.advertisement) {
      this.imagesService.getThumbnailAdvertisementId(this.advertisement.id).subscribe({
        next: res => this.thumbnail = res,
      });
    }
  }
}
