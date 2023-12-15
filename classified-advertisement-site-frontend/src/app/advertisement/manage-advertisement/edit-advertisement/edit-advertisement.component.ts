import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AdvertisementResponse, CategoryResponse } from 'src/app/openapi/advertisementservice';
import { EditAdvertisement } from '../manage-advertisement.component';

@Component({
  selector: 'app-edit-advertisement',
  templateUrl: './edit-advertisement.component.html',
  styleUrls: ['./edit-advertisement.component.scss']
})
export class EditAdvertisementComponent implements OnInit {
  @Input() advertisement?: EditAdvertisement;
  @Input() categories?: CategoryResponse[];
  @Output() next: EventEmitter<EditAdvertisement> = new EventEmitter();
  @Input() newAdvertisement?: boolean;
  title: string = '';
  description: string = '';
  price: number = 0;
  categoryId?: number;
  status?: AdvertisementResponse.StatusEnum;
  advertisementType?: AdvertisementResponse.TypeEnum;
  allStatuses: AdvertisementResponse.StatusEnum[] = [];
  allTypes = Object.values(AdvertisementResponse.TypeEnum); 
  expiration?: Date;
  private initialStatus?: AdvertisementResponse.StatusEnum;

  ngOnInit(): void {
    if (this.advertisement) {
      this.title = this.advertisement.title;
      this.description = this.advertisement.description;
      this.price = this.advertisement.price;
      this.categoryId = this.advertisement.categoryId;
      if (this.advertisement.status) {
        this.status = this.advertisement.status;
      }
    }

    this.initialStatus = this.advertisement?.status;
    this.allStatuses = this.availableStatuses();
  }

  onNext() {
    this.next.emit({
      id: this.advertisement?.id,
      title: this.title,
      description: this.description,
      price: this.price,
      categoryId: this.categoryId,
      status: this.status,
      type: this.advertisementType,
      expiration: this.advertisementType === AdvertisementResponse.TypeEnum.Bid ? this.expiration : undefined,
    });
  }

  private availableStatuses() {
    switch (this.initialStatus) {
      case AdvertisementResponse.StatusEnum.Available:
      case AdvertisementResponse.StatusEnum.Freezed:
        return [
          AdvertisementResponse.StatusEnum.Available,
          AdvertisementResponse.StatusEnum.Freezed,
          AdvertisementResponse.StatusEnum.Sold,
        ];
      case AdvertisementResponse.StatusEnum.Sold:
        return [
          AdvertisementResponse.StatusEnum.Sold,
        ];
      case AdvertisementResponse.StatusEnum.Archived:
        return [
          AdvertisementResponse.StatusEnum.Archived,
        ];
      case AdvertisementResponse.StatusEnum.Bidding:
        return [
          AdvertisementResponse.StatusEnum.Bidding,
          AdvertisementResponse.StatusEnum.Sold,
          AdvertisementResponse.StatusEnum.Archived,
        ];
      default:
        return [];
    }
  }
}
