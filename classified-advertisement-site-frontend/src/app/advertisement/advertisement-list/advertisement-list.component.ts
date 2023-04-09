import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AdvertisementResponse, AdvertisementService, CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-advertisement-list',
  templateUrl: './advertisement-list.component.html',
  styleUrls: ['./advertisement-list.component.scss']
})
export class AdvertisementListComponent implements OnInit {
  private categoryId?: number;
  advertisements?: AdvertisementResponse[];
  category: CategoryResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private readonly advertisementService: AdvertisementService,
    private readonly categoryService: CategoryService,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.categoryId = +params['id'];
      this.advertisementService.getAdvertisements(this.categoryId).subscribe({
        next: advertisements => this.advertisements = advertisements,
      });
      this.categoryService.getCategories().subscribe({
        next: res => { 
          if (this.categoryId) {
            this.setCategory(res, this.categoryId);
          }
        },
      });
    });
  }

  search(query: string) {
    if (this.categoryId && query.length > 2) {
      this.advertisementService.getCategoryIdSearchQuery(this.categoryId, query).subscribe({
        next: res => this.advertisements = res,
      });
    }
  }

  private setCategory(categories: CategoryResponse[], id: number) {
    let parentCategoryId: number | undefined = id;
    this.category = [];
    do {
      const currentCategory = categories.find(e => e.id === parentCategoryId);
      if (currentCategory) {
        this.category.unshift(currentCategory);
        parentCategoryId = currentCategory.parentCategoryId;
      }
    } while (parentCategoryId);
  }
}
