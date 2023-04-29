import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';
import { ScraperService, SiteRequest, SiteResponse } from 'src/app/openapi/webscraperservice';
import { EditSiteDialogComponent } from './edit-site-dialog/edit-site-dialog.component';

@Component({
  selector: 'app-web-scraper',
  templateUrl: './web-scraper.component.html',
  styleUrls: ['./web-scraper.component.scss']
})
export class WebScraperComponent implements OnInit {
  displayedColumns: string[] = ['name', 'category', 'url'];
  sites: SiteResponse[] = [];
  categories: CategoryResponse[] = [];

  constructor(
    private readonly scraperService: ScraperService,
    private readonly categoryService: CategoryService,
    private readonly dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.scraperService.scraperControllerGetAllSites().subscribe({
      next: sites => this.sites = sites,
    });
    this.categoryService.getCategories().subscribe({
      next: res => this.categories = res,
    });
  }

  getCategoryNameById(id: number): string | undefined {
    return this.categories.find(c => c.id === id)?.name;
  }

  addNewSite() {
    const dialogRef = this.dialog.open(EditSiteDialogComponent, {
      width: '600px',
      data: { categories: this.categories },
    });

    dialogRef.afterClosed().subscribe((newSite?: SiteRequest) => {
      if (newSite) {
        this.scraperService.scraperControllerCreateSite(newSite).subscribe({
          next: res => this.sites = [...this.sites, res],
        });
      }
    });
  }
}
