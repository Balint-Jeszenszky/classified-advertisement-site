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
      from: 'user@outlook.com', // Senders email address
      subject: 'Testing Nest Mailermodule with template ✔',
      template: 'index', // The `.pug` or `.hbs` extension is appended automatically.
      context: {  // Data to be sent to template engine.
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
