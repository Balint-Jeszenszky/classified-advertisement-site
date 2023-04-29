import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CategoryResponse } from 'src/app/openapi/advertisementservice';
import { SiteRequest, SiteResponse } from 'src/app/openapi/webscraperservice';

@Component({
  selector: 'app-edit-site-dialog',
  templateUrl: './edit-site-dialog.component.html',
  styleUrls: ['./edit-site-dialog.component.scss']
})
export class EditSiteDialogComponent {
  name: string = '';
  url: string = '';
  categoryId?: number;
  selectorBase: string = '';
  selectorImageProperty: string = '';
  selectorImageSelector: string = '';
  selectorPriceProperty: string = '';
  selectorPriceSelector: string = '';
  selectorTitleProperty: string = '';
  selectorTitleSelector: string = '';
  selectorUrlProperty: string = '';
  selectorUrlSelector: string = '';
  categories: CategoryResponse[] = [];
  siteId?: string;

  constructor(
    private readonly dialogRef: MatDialogRef<EditSiteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: { categories: CategoryResponse[], site?: SiteResponse },
  ) { }

  ngOnInit(): void {
    this.categories = this.data.categories;

    if (this.data.site) {
      this.name = this.data.site.name;
      this.url = this.data.site.url;
      this.categoryId = this.data.site.categoryId;
      this.selectorBase = this.data.site.selector.base;
      this.selectorImageProperty = this.data.site.selector.image.property;
      this.selectorImageSelector = this.data.site.selector.image.selector;
      this.selectorPriceProperty = this.data.site.selector.price.property;
      this.selectorPriceSelector = this.data.site.selector.price.selector;
      this.selectorTitleProperty = this.data.site.selector.title.property;
      this.selectorTitleSelector = this.data.site.selector.title.selector;
      this.selectorUrlProperty = this.data.site.selector.url.property;
      this.selectorUrlSelector = this.data.site.selector.url.selector;
      this.siteId = this.data.site.id;
    }
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  getSiteData(): SiteRequest | undefined {
    if (!this.categoryId) {
      return undefined;
    }

    return {
      name: this.name,
      url: this.url,
      categoryId: this.categoryId,
      selector: {
        base: this.selectorBase,
        image: {
          property: this.selectorImageProperty,
          selector: this.selectorImageSelector,
        },
        price: {
          property: this.selectorPriceProperty,
          selector: this.selectorPriceSelector,
        },
        title: {
          property: this.selectorTitleProperty,
          selector: this.selectorTitleSelector,
        },
        url: {
          property: this.selectorUrlProperty,
          selector: this.selectorUrlSelector,
        },
      },
    };
  }
}
