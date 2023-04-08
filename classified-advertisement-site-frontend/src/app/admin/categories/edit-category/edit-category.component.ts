import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { CategoryRequest, CategoryResponse } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-edit-category',
  templateUrl: './edit-category.component.html',
  styleUrls: ['./edit-category.component.scss']
})
export class EditCategoryComponent implements OnInit {
  @Input() categoriesObservable?: Observable<CategoryResponse[]>;
  @Input() categoryIdObservable?: Observable<number | undefined>;
  @Output() saveEvent: EventEmitter<CategoryRequest | CategoryResponse>= new EventEmitter();
  @Output() cancelEvent: EventEmitter<void>= new EventEmitter();
  @Output() deleteEvent: EventEmitter<number>= new EventEmitter();
  categoryId?: number;
  categories?: CategoryResponse[];
  categoryName: string = '';
  parentCategoryId?: number;

  ngOnInit(): void {
    this.categoriesObservable?.subscribe(res => {
      this.categories = res;
      this.setData();
    });
    this.categoryIdObservable?.subscribe(res => {
      this.categoryId = res;
      this.setData();
    });
  }

  private setData() {
    const category = this.categories?.find(c => c.id === this.categoryId);
    if (category) {
      this.categoryName = category.name;
      this.parentCategoryId = category.parentCategoryId;
    }
  }

  saveCategory() {
    this.saveEvent.emit({
      id: this.categoryId,
      name: this.categoryName,
      parentCategoryId: this.parentCategoryId,
    });
  }

  cancel() {
    this.cancelEvent.emit();
  }

  deleteCategory() {
    if (confirm(`Delete "${this.categoryName}" and all subcategories?`)) {
      this.deleteEvent.emit(this.categoryId);
    }
  }
}
