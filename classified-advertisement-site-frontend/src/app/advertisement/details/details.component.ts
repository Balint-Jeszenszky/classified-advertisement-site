import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AdvertisementResponse, AdvertisementService, CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';
import { ImagesService } from 'src/app/openapi/imageprocessingservice';
import { PublicUserDetailsResponse, PublicUserService } from 'src/app/openapi/userservice';
import { ProductResponse, ScraperService } from 'src/app/openapi/webscraperservice';
import { LoggedInUserService } from 'src/app/service/logged-in-user.service';
import { Role } from 'src/app/service/types';
import { LiveBidService } from '../service/live-bid.service';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.scss']
})
export class DetailsComponent implements OnInit, OnDestroy {
  id?: number;
  advertisement?: AdvertisementResponse;
  category: CategoryResponse[] = [];
  advertiser?: PublicUserDetailsResponse;
  admin: boolean = false;
  userId?: number;
  imageUrls?: string[];
  commercialPrice?: ProductResponse;
  bid: number = 0;
  bidWinnerUser?: PublicUserDetailsResponse;
  expired: boolean = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly advertisementService: AdvertisementService,
    private readonly categoryService: CategoryService,
    private readonly loggedInUserService: LoggedInUserService,
    private readonly publicUserService: PublicUserService,
    private readonly imagesService: ImagesService,
    private readonly scraperService: ScraperService,
    private readonly liveBidService: LiveBidService,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = +params['id'];
      this.advertisementService.getAdvertisementId(this.id).subscribe(ad => {
        this.advertisement = ad;
        this.expired = !!ad.expiration && Date.parse(ad.expiration) < Date.now();
        this.categoryService.getCategories().subscribe({
          next: cat => this.setCategory(cat, ad.categoryId),
        });
        this.loggedInUserService.user.subscribe(user => {
          this.admin = !!user?.roles.includes(Role.ROLE_ADMIN);
          this.userId = user?.id;
        });
        this.publicUserService.getUserId([ad.advertiserId]).subscribe({
          next: users => this.advertiser = users[0],
        });
        if (this.advertisement.type === AdvertisementResponse.TypeEnum.Bid) {
          this.liveBidService.subscribeForBids(this.advertisement.id).subscribe({
            next: bid => {
              if (this.advertisement?.price) {
                this.advertisement.price = bid.price;
              }
  
              this.publicUserService.getUserId([bid.userId]).subscribe({
                next: users => this.bidWinnerUser = users[0],
              });
            }
          });
        }
      });
      this.imagesService.getImageListAdvertisementId(this.id).subscribe({
        next: urls => this.imageUrls = urls,
      });
      this.scraperService.scraperControllerGetProductByAdvertisementId(this.id).subscribe({
        next: res => this.commercialPrice = res,
      });
    });
  }

  ngOnDestroy(): void {
    this.liveBidService.unsubscribe();
  }

  onDelete() {
    if (this.id && confirm("Delete advertisement?")) {
      this.advertisementService.deleteAdvertisementId(this.id).subscribe({
        next: () => this.router.navigate(['/category/', this.advertisement?.categoryId]),
      });
    }
  }

  submitBid() {
    if (!this.advertisement || this.bid <= this.advertisement.price) {
      return;
    }

    this.liveBidService.bid(this.advertisement.id, this.bid);
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
