import { Component, OnInit } from '@angular/core';
import { ReplaySubject } from 'rxjs';
import { CategoryRequest, CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.scss']
})
export class CategoriesComponent implements OnInit {
  private categories?: CategoryResponse[];
  selectedCategoryId?: number;
  private categoriesSubject: ReplaySubject<CategoryResponse[]> = new ReplaySubject(1);
  private selectedCategoryIdSubject: ReplaySubject<number | undefined> = new ReplaySubject(1);

  get categoriesObservable() {
    return this.categoriesSubject.asObservable();
  }

  get selectedCategoryIdObservable() {
    return this.selectedCategoryIdSubject.asObservable();
  }

  constructor(
    private readonly categoryService: CategoryService,
  ) { }

  ngOnInit(): void {
    this.categoryService.getCategories().subscribe({
      next: res => {
        this.updateCategories(res);
      },
    });
  }

  editCategory(id?: number) {
    this.updateCategoryId(id);
  }

  saveCategory(category: CategoryRequest | CategoryResponse) {
    if ('id' in category) {
      this.categoryService.putCategories(category.id, {
        name: category.name,
        parentCategoryId: category.parentCategoryId,
      }).subscribe(res => {
        const index = this.categories?.findIndex(c => c.id === category.id);
        if (this.categories && index && index !== -1) {
          this.updateCategories([...this.categories.slice(0, index), res, ...this.categories.slice(index + 1)]);
        }
        this.updateCategoryId();
      });
    } else {
      this.categoryService.postCategories(category).subscribe(res => {
        if (this.categories) this.updateCategories([...this.categories, res]);
        this.updateCategoryId();
      });
    }
  }

  deleteCategory(id: number) {
    this.categoryService.deleteCategories(id).subscribe({
      next: () => {
        if (this.categories) this.updateCategories(this.categories.filter(c => c.id !== id));
        this.updateCategoryId();
      },
    });
  }

  private updateCategories(categories: CategoryResponse[]) {
    this.categories = categories;
    this.categoriesSubject.next(categories);
  }

  private updateCategoryId(id?: number) {
    this.selectedCategoryId = id;
    this.selectedCategoryIdSubject.next(id);
  }
}
