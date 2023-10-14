import { Component, OnInit } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { ActivatedRoute } from '@angular/router';
import { AdvertisementResponse, AdvertisementService, CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';
import { ImagesService } from 'src/app/openapi/imageprocessingservice';

export interface EditAdvertisement {
  id?: number;
  title: string;
  description: string;
  price: number;
  categoryId?: number;
  status?: AdvertisementResponse.StatusEnum;
  type?: AdvertisementResponse.TypeEnum;
}

@Component({
  selector: 'app-manage-advertisement',
  templateUrl: './manage-advertisement.component.html',
  styleUrls: ['./manage-advertisement.component.scss']
})
export class ManageAdvertisementComponent implements OnInit {
  newAdvertisement: boolean = true;
  advertisement?: EditAdvertisement;
  categories?: CategoryResponse[];
  files: File[] = [];
  images: string[] = [];
  imagesToDelete: string[] = [];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly categoryService: CategoryService,
    private readonly advertisementService: AdvertisementService,
    private readonly imagesService: ImagesService,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      let advertisementId;
      if (params['id']) {
        advertisementId = +params['id'];
        this.newAdvertisement = false;
      }
      if (advertisementId) {
        this.advertisementService.getAdvertisementId(advertisementId).subscribe({
          next: res => this.advertisement = res,
        });
        this.imagesService.getImageListAdvertisementId(advertisementId).subscribe({
          next: res => this.images = res,
        });
      } else {
        this.advertisement = {
          title: '',
          description: '',
          price: 0,
        }
      }
    });
    this.categoryService.getCategories().subscribe({
      next: res => this.categories = res,
    });
  }

  imageUpload(stepper: MatStepper, advertisement: EditAdvertisement) {
    this.advertisement = advertisement;
    stepper.next();
  }

  editDetails(stepper: MatStepper, files: File[]) {
    this.files = files;
    stepper.previous();
  }

  save(stepper: MatStepper, files: File[]) {
    this.files = files;
    if (!this.advertisement || !this.advertisement.categoryId) {
      return;
    }

    if (this.newAdvertisement && this.advertisement.type) {
      this.advertisementService.postAdvertisements(
        this.advertisement.title,
        this.advertisement.description,
        this.advertisement.price,
        this.advertisement.categoryId,
        this.advertisement.type,
        this.files,
      ).subscribe({
        next: res => {
          this.advertisement = res;
          stepper.next();
        },
      })
    } else if (this.advertisement.id && this.advertisement.status) {
      this.advertisementService.putAdvertisementId(
        this.advertisement.id,
        this.advertisement.title,
        this.advertisement.description,
        this.advertisement.price,
        this.advertisement.categoryId,
        this.advertisement.status,
        this.files,
        this.imagesToDelete.join(';'),
      ).subscribe({
        next: res => {
          this.advertisement = res;
          stepper.next();
        },
      });
    }
  }
}
