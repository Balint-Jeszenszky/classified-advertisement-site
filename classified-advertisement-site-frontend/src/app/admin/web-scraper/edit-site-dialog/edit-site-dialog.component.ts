import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CategoryResponse } from 'src/app/openapi/advertisementservice';
import { SiteRequest, SiteResponse } from 'src/app/openapi/webscraperservice';
import { CategoryTree, createCategoryTree } from 'src/app/util/category-tree';

@Component({
  selector: 'app-edit-site-dialog',
  templateUrl: './edit-site-dialog.component.html',
  styleUrls: ['./edit-site-dialog.component.scss']
})
export class EditSiteDialogComponent {
  name: string = '';
  url: string = '';
  categoryIds: number[] = [];
  selectorBase: string = '';
  selectorImageProperty: string = '';
  selectorImageSelector: string = '';
  selectorPriceProperty: string = '';
  selectorPriceSelector: string = '';
  selectorTitleProperty: string = '';
  selectorTitleSelector: string = '';
  selectorUrlProperty: string = '';
  selectorUrlSelector: string = '';
  categoryTree: CategoryTree[] = [];
  siteId?: string;

  constructor(
    private readonly dialogRef: MatDialogRef<EditSiteDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: { categories: CategoryResponse[], site?: SiteResponse },
  ) { }

  ngOnInit(): void {
    this.categoryTree = createCategoryTree(this.data.categories);

    if (this.data.site) {
      this.name = this.data.site.name;
      this.url = this.data.site.url;
      this.categoryIds = this.data.site.categoryIds;
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

  handleChange() {
    if (this.categoryIds.some(e => !e)) {
      this.categoryIds = [];
      return;
    }

    this.categoryIds = this.categoryIds.map(c => this.getChildrenCategoryIds(this.categoryTree.filter(t => t.id === c))).flat();
  }

  private getChildrenCategoryIds(categoryTree: CategoryTree[] | undefined): number[] {
    if (!categoryTree) {
      return [];
    }

    return categoryTree.map(c => [c.id, ...this.getChildrenCategoryIds(c.children)]).flat();
  }

  getSiteData(): SiteRequest | undefined {
    if (!this.categoryIds.length) {
      return undefined;
    }

    return {
      name: this.name,
      url: this.url,
      categoryIds: this.categoryIds,
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
