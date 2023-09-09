import { MailerService } from '@nestjs-modules/mailer';
import { Injectable } from '@nestjs/common';
import { setVapidDetails, sendNotification } from 'web-push';

@Injectable()
export class NotificationService {

  constructor(
    private readonly mailerService: MailerService,
  ) {
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

  sendEmail() {
    this.mailerService.sendMail({
      to: 'user@gmail.com', // List of receivers email address
      from: process.env.EMAIL_USER,
      subject: 'Testing Nest Mailermodule with template ✔',
      template: 'test',
      context: {
        code: 'cf1a3f828287',
        username: 'john doe',
      },
    })
    .then((success) => {
      console.log(success);
    })
    .catch((err) => {
      console.log(err);
    });
  }
}
