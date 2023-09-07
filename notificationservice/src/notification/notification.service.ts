import { Injectable } from '@nestjs/common';
import { setVapidDetails, sendNotification } from 'web-push';

@Injectable()
export class NotificationService {

  constructor() {
    setVapidDetails(
      `mailto:${process.env.ADMIN_EMAIL}`,
      process.env.VAPID_PUBLIC_KEY,
      process.env.VAPID_PRIVATE_KEY,
    );
  }

  sendWebPushNotification() {
    // sendNotification()
  }

  getPublicVapidKey(): string {
    return process.env.VAPID_PUBLIC_KEY;
  }
}
