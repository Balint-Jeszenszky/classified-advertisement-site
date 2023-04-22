import { Component, Input, OnInit } from '@angular/core';
import { AdvertisementResponse } from 'src/app/openapi/advertisementservice';
import { ImagesService } from 'src/app/openapi/imageprocessingservice';

@Component({
  selector: 'app-advertisement-list-item',
  templateUrl: './advertisement-list-item.component.html',
  styleUrls: ['./advertisement-list-item.component.scss']
})
export class AdvertisementListItemComponent implements OnInit {
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
