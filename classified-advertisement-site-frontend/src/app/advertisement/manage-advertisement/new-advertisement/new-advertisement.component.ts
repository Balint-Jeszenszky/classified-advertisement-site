import { Component, EventEmitter, Output } from '@angular/core';
import { AdvertisementService, CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-new-advertisement',
  templateUrl: './new-advertisement.component.html',
  styleUrls: ['./new-advertisement.component.scss']
})
export class NewAdvertisementComponent {
  @Output() saved: EventEmitter<number> = new EventEmitter();
  title: string = '';
  description: string = '';
  price: number = 0;
  categoryId?: number;
  categories: CategoryResponse[] = [];

  constructor(
    private readonly categoryService: CategoryService,
    private readonly advertisementService: AdvertisementService,
  ) { }

  ngOnInit(): void {
    this.categoryService.getCategories().subscribe({
      next: res => this.categories = res,
    });
  }

  save() {
    if (this.categoryId) {
      this.advertisementService.postAdvertisements(
        this.title,
        this.description,
        this.price,
        this.categoryId,
      ).subscribe({
        next: res => this.saved.emit(res.id),
      });
    }
  } 
}
