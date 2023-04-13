import { Component, OnInit } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { ActivatedRoute } from '@angular/router';
import { AdvertisementResponse, AdvertisementService, CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';

export interface EditAdvertisement {
  id?: number;
  title: string;
  description: string;
  price: number;
  categoryId?: number;
  status?: AdvertisementResponse.StatusEnum;
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

  constructor(
    private readonly route: ActivatedRoute,
    private readonly categoryService: CategoryService,
    private readonly advertisementService: AdvertisementService,
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
      } else {
        this.advertisement = { title: '', description: '', price: 0 };
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

    if (this.newAdvertisement) {
      this.advertisementService.postAdvertisements(
        this.advertisement.title,
        this.advertisement.description,
        this.advertisement.price,
        this.advertisement.categoryId,
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
        // [1,2], TODO find out how to send array
      ).subscribe({
        next: res => {
          this.advertisement = res;
          stepper.next();
        },
      });
    }
  }
}
