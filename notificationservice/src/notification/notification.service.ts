import { MailerService } from '@nestjs-modules/mailer';
import { Injectable, Logger } from '@nestjs/common';
import { SentMessageInfo } from 'nodemailer';
import { setVapidDetails, sendNotification } from 'web-push';
import { Email, Push } from './dto/Notification.dto';

@Injectable()
export class NotificationService {
  private readonly logger: Logger = new Logger(NotificationService.name);

  constructor(
    private readonly mailerService: MailerService,
  ) {
    setVapidDetails(
      `mailto:${process.env.ADMIN_EMAIL}`,
      process.env.VAPID_PUBLIC_KEY,
      process.env.VAPID_PRIVATE_KEY,
    );
  }

  async sendWebPushNotification(push: Push) {
    this.logger.log('push')
    // sendNotification()
  }

  getPublicVapidKey(): string {
    return process.env.VAPID_PUBLIC_KEY;
  }

  sendEmail(email: Email): Promise<SentMessageInfo> {
    return this.mailerService.sendMail({
      to: email.toAddress,
      from: process.env.EMAIL_USER,
      subject: 'TODO read from json (?)',
      template: email.template,
      context: {
        baseurl: process.env.BASEURL,
        ...email.data
      },
    });
  }
}
