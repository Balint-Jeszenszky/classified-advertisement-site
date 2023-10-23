import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CategoryResponse, CategoryService } from 'src/app/openapi/advertisementservice';
import { ScraperService, SiteRequest, SiteResponse } from 'src/app/openapi/webscraperservice';
import { EditSiteDialogComponent } from './edit-site-dialog/edit-site-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-web-scraper',
  templateUrl: './web-scraper.component.html',
  styleUrls: ['./web-scraper.component.scss']
})
export class WebScraperComponent implements OnInit {
  displayedColumns: string[] = ['name', 'category', 'url', 'edit'];
  sites: SiteResponse[] = [];
  categories: CategoryResponse[] = [];

  constructor(
    private readonly scraperService: ScraperService,
    private readonly categoryService: CategoryService,
    private readonly dialog: MatDialog,
    private readonly snackBar: MatSnackBar,
  ) { }

  ngOnInit(): void {
    this.scraperService.scraperControllerGetAllSites().subscribe({
      next: sites => this.sites = sites,
    });
    this.categoryService.getCategories().subscribe({
      next: res => this.categories = res,
    });
  }

  getCategoryNameByIds(ids: number[]): string {
    return ids.map(id => this.categories.find(c => c.id === id)?.name).join(', ');
  }

  editSite(site?: SiteResponse) {
    const dialogRef = this.dialog.open(EditSiteDialogComponent, {
      width: '600px',
      data: {
        categories: this.categories,
        site,
      },
    });

    dialogRef.afterClosed().subscribe((siteData?: { siteId?: string, site: SiteRequest }) => {
      if (siteData?.siteId) {
        this.scraperService.scraperControllerUpdateSite(siteData.siteId, siteData.site).subscribe({
          next: res => {
            const idx = this.sites.findIndex(s => s.id === res.id);
            if (!~idx) {
              this.sites = [...this.sites, res];
            }
            this.sites = [
              ...this.sites.slice(0, idx),
              res,
              ...this.sites.slice(idx + 1)
            ];
          },
        });
      } else if (siteData) {
        this.scraperService.scraperControllerCreateSite(siteData.site).subscribe({
          next: res => this.sites = [...this.sites, res],
        });
      }
    });
  }

  triggerSite(site: SiteResponse) {
    this.scraperService.scraperControllerScrapeSite({ siteId: site.id }).subscribe({
      next: () => this.snackBar.open('Scraper triggered', 'OK', { duration: 5000 }),
    });
  }

  deleteSite(site: SiteResponse) {
    if (confirm(`Delete site "${site.name}"?`)) {
      this.scraperService.scraperControllerDeleteSite(site.id).subscribe({
        next: () => this.sites = this.sites.filter(s => s.id !== site.id),
      });
    }
  }
}
