import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AdvertisementResponse, AdvertisementService, CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';
import { BidService } from 'src/app/openapi/bidservice';
import { LoadingState } from 'src/app/shared/components/spinner/spinner.component';

@Component({
  selector: 'app-advertisement-list',
  templateUrl: './advertisement-list.component.html',
  styleUrls: ['./advertisement-list.component.scss']
})
export class AdvertisementListComponent implements OnInit {
  categoryId?: number;
  advertisements?: AdvertisementResponse[];
  category: CategoryResponse[] = [];
  searchTerm: string = '';
  loadingState: LoadingState = LoadingState.LOADING;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly advertisementService: AdvertisementService,
    private readonly categoryService: CategoryService,
    private readonly bidService: BidService,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.categoryId = +params['id'];
      this.searchTerm = params['query'] || '';

      if (this.categoryId) {
        this.categoryService.getCategories().subscribe({
          next: res => { 
            if (this.categoryId) {
              this.setCategory(res, this.categoryId);
            }
          },
        });
      }

      if (this.searchTerm) {
        this.search(this.searchTerm);
      } else {
        this.loadAdvertisementByCategory();
      }
    });
  }

  search(query: string) {
    this.searchTerm = query;

    if (this.searchTerm.length < 3) {
      return;
    }

    if (this.categoryId) {
      this.searchByCategory();
    } else {
      this.searchAll();
    }
  }

  loadAdvertisementByCategory() {
    if (!this.categoryId) {
      return;
    }

    this.advertisementService.getAdvertisements(this.categoryId).subscribe({
      next: advertisements => {
        this.loadingState = LoadingState.LOADED;
        this.advertisements = advertisements;
        this.loadBids();
      },
      error: () => {
        this.loadingState = LoadingState.ERROR;
      },
    });
  }

  loadBids() {
    if (!this.advertisements) {
      return;
    }

    const ids = this.advertisements.filter(a => a.type === AdvertisementResponse.TypeEnum.Bid).map(a => a.id);

    if (!ids.length) {
      return;
    }

    this.bidService.getCurrentBidsIds(ids).subscribe(res => {
      res.forEach(bid => {
        const advertisement = this.advertisements?.find(a => a.id === bid.id);
        if (advertisement) {
          advertisement.price = bid.price;
        }
      });
    });
  }

  searchAll() {
    this.router.navigate(['/search/', this.searchTerm]);

    this.loadingState = LoadingState.LOADING;
    this.advertisements = undefined;
    this.advertisementService.getAdvertisementsSearchQuery(this.searchTerm).subscribe({
      next: advertisements => {
        this.loadingState = LoadingState.LOADED;
        this.advertisements = advertisements;
        this.loadBids();
      },
      error: () => {
        this.loadingState = LoadingState.ERROR;
      },
    });
  }

  searchByCategory() {
    if (!this.categoryId) {
      return;
    }

    this.router.navigate(['/category/', this.categoryId, this.searchTerm]);

    this.loadingState = LoadingState.LOADING;
    this.advertisements = undefined;
    this.advertisementService.getCategoryIdSearchQuery(this.categoryId, this.searchTerm).subscribe({
      next: advertisements => {
        this.loadingState = LoadingState.LOADED;
        this.advertisements = advertisements;
        this.loadBids();
      },
      error: () => {
        this.loadingState = LoadingState.ERROR;
      },
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
