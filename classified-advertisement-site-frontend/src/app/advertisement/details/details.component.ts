import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AdvertisementResponse, AdvertisementService, CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';
import { LoggedInUserService } from 'src/app/service/logged-in-user.service';
import { Role } from 'src/app/service/types';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.scss']
})
export class DetailsComponent implements OnInit {
  id?: number;
  advertisement?: AdvertisementResponse;
  category: CategoryResponse[] = [];
  admin: boolean = false;
  owner: boolean = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly advertisementService: AdvertisementService,
    private readonly categoryService: CategoryService,
    private readonly loggedInUserService: LoggedInUserService,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = +params['id'];
      this.advertisementService.getAdvertisementId(this.id).subscribe(ad => {
        this.advertisement = ad;
        this.categoryService.getCategories().subscribe({
          next: cat => this.setCategory(cat, ad.categoryId),
        });
        this.loggedInUserService.user.subscribe(user => {
          if (user) {
            this.owner = user.id === ad.advertiserId;
            this.admin = user.roles.includes(Role.ROLE_ADMIN);
          }
        })
      });
    });
  }

  onDelete() {
    if (this.id && confirm("Delete advertisement?")) {
      this.advertisementService.deleteAdvertisementId(this.id).subscribe({
        next: () => this.router.navigate(['/category/', this.advertisement?.categoryId]),
      });
    }
  }

  private setCategory(categories: CategoryResponse[], id?: number) {
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
