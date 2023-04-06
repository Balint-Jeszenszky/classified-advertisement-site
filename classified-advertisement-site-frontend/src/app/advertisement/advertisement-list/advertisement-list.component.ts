import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AdvertisementResponse, AdvertisementService, CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';

@Component({
  selector: 'app-advertisement-list',
  templateUrl: './advertisement-list.component.html',
  styleUrls: ['./advertisement-list.component.scss']
})
export class AdvertisementListComponent implements OnInit {
  advertisements?: AdvertisementResponse[];
  category: CategoryResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private readonly advertisementService: AdvertisementService,
    private readonly categoryService: CategoryService,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      this.advertisementService.getAdvertisements(id).subscribe({
        next: advertisements => this.advertisements = advertisements,
      });
      this.categoryService.getCategories().subscribe({
        next: res => this.setCategory(res, id),
      });
    });
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
