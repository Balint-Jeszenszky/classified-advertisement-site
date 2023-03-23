import { Injectable } from '@angular/core';
import { UserManagementService, UserProfileService } from './userservice';

@Injectable({
  providedIn: 'root'
})
export class CredentialsService {
  private services;

  constructor(
    userManagementService: UserManagementService,
    userProfileService: UserProfileService,
  ) {
    this.services = [
      userManagementService,
      userProfileService,
    ];
  }

  updateCredentials(token: string) {
    this.services.forEach(s => s.configuration.credentials['JWT'] = token);
  }
}
