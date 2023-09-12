import { MailerService } from '@nestjs-modules/mailer';
import { Injectable, Logger } from '@nestjs/common';
import { SentMessageInfo } from 'nodemailer';
import { setVapidDetails, sendNotification } from 'web-push';
import { Email, Push } from './dto/Notification.dto';
import * as emailSubject from '../../templates/email/subject.json';
import { User } from 'src/auth/User.model';
import { PushSubscriptionRequest } from './dto/PushSubscriptionRequest.dto';
import { PushSubscription, PushSubscriptionDocument } from './schema/push-subscription.model';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';

@Injectable()
export class NotificationService {
  private readonly logger: Logger = new Logger(NotificationService.name);

  constructor(
    @InjectModel(PushSubscription.name) private readonly pushSubscription: Model<PushSubscription>,
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

  async subscribePushNotification(user: User, subscriptionReuest: PushSubscriptionRequest): Promise<void> {
    const subscription = await this.getOrCreateUserSubscription(user.id);

    if (subscription.subscriptions.some(s => s.endpoint === subscriptionReuest.endpoint)) {
      return;
    }

    subscription.subscriptions.push(subscriptionReuest);
    await subscription.save();
  }

  private async getOrCreateUserSubscription(userId: number): Promise<PushSubscriptionDocument> {
    const userSubscriptions = await this.pushSubscription.findOne({ userId }).exec();

    if (userSubscriptions) {
      return userSubscriptions;
    }

    return new this.pushSubscription(new PushSubscription(userId));
  }

  sendEmail(email: Email): Promise<SentMessageInfo> {
    return this.mailerService.sendMail({
      to: email.toAddress,
      from: process.env.EMAIL_USER,
      subject: emailSubject[email.template],
      template: email.template,
      context: {
        baseurl: process.env.BASEURL,
        ...email.data
      },
    });
  }
}
