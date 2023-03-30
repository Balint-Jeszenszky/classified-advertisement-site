import { Injectable } from '@angular/core';
import { AdvertisementService, CommentService } from './advertisementservice';
import { UserManagementService, UserProfileService } from './userservice';

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
  ) {
    this.services = [
      userManagementService,
      userProfileService,
      advertisementService,
      commentService,
    ];
  }

  updateCredentials(token: string) {
    this.services.forEach(s => s.configuration.credentials['JWT'] = token);
  }
}
