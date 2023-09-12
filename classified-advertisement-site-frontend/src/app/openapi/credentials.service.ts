import { Injectable } from '@angular/core';
import { AdvertisementService, CommentService } from './advertisementservice';
import { UserManagementService, UserProfileService } from './userservice';
import { ScraperService } from './webscraperservice';
import { NotificationsService } from './notificationservice';

@Injectable({
  providedIn: 'root'
})
export class CredentialsService {
  private services;

  constructor(
    userManagementService: UserManagementService,
    userProfileService: UserProfileService,
    advertisementService: AdvertisementService,
    commentService: CommentService,
    scraperService: ScraperService,
    notificationService: NotificationsService,
  ) {
    this.services = [
      userManagementService,
      userProfileService,
      advertisementService,
      commentService,
      scraperService,
      notificationService,
    ];
  }

  updateCredentials(token: string) {
    this.services.forEach(s => s.configuration.credentials['JWT'] = token);
  }
}
