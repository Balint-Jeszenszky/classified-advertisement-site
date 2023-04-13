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
  title: string = '';
  description: string = '';
  price: number = 0;
  categoryId?: number;
  status?: AdvertisementResponse.StatusEnum;
  allStatuses = Object.values(AdvertisementResponse.StatusEnum);

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
  }

  onNext() {
    this.next.emit({
      id: this.advertisement?.id,
      title: this.title,
      description: this.description,
      price: this.price,
      categoryId: this.categoryId,
      status: this.status,
    });
  }
}
