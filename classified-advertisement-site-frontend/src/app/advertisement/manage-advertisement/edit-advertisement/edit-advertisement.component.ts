import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AdvertisementRequest, AdvertisementService, CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-edit-advertisement',
  templateUrl: './edit-advertisement.component.html',
  styleUrls: ['./edit-advertisement.component.scss']
})
export class EditAdvertisementComponent implements OnInit {
  @Input() advertisementId?: number;
  @Output() saved: EventEmitter<void> = new EventEmitter();
  title: string = '';
  description: string = '';
  price: number = 0;
  categoryId?: number;
  categories: CategoryResponse[] = [];
  status: AdvertisementRequest.StatusEnum = AdvertisementRequest.StatusEnum.Available;
  allStatuses = Object.values(AdvertisementRequest.StatusEnum);

  constructor(
    private readonly categoryService: CategoryService,
    private readonly advertisementService: AdvertisementService,
  ) { }

  ngOnInit(): void {
    this.categoryService.getCategories().subscribe({
      next: res => this.categories = res,
    });
    if (this.advertisementId) {
      this.advertisementService.getAdvertisementId(this.advertisementId).subscribe({
        next: res => {
          this.title = res.title;
          this.description = res.description;
          this.price = res.price;
          this.categoryId = res.categoryId;
          this.status = res.status;
        },
      });
    }
  }

  save() {
    if (this.advertisementId && this.categoryId) {
      this.advertisementService.putAdvertisementId(this.advertisementId, {
        title: this.title,
        description: this.description,
        price: this.price,
        categoryId: this.categoryId,
        status: this.status,
      }).subscribe({
        next: () => this.saved.emit(),
      });
    }
  } 
}
