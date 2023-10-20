import { MailerService } from '@nestjs-modules/mailer';
import { Injectable, Logger } from '@nestjs/common';
import { SentMessageInfo } from 'nodemailer';
import { setVapidDetails, sendNotification } from 'web-push';
import { Email, Push } from './dto/Notification.dto';
import * as emailSubject from '../../templates/email/subject.json';
import * as pushTemplate from '../../templates/push/pushNotifications.json';
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

  async sendWebPushNotification(push: Push): Promise<void> {
    const user = await this.pushSubscription.findOne({ userId: push.userId }).exec();

    if (!user) {
      return;
    }

    const payload = this.preparePushNotification(push);

    await Promise.all(user.subscriptions.map(
      s => sendNotification(s, payload)
        .catch(async err => await this.deletePushNotificationEndpoint(user, err.endpoint))
    ));
  }

  private async deletePushNotificationEndpoint(user: PushSubscriptionDocument, endpoint: string) {
    user.subscriptions = user.subscriptions.filter(s => s.endpoint !== endpoint);
    await user.save();
  }

  private preparePushNotification(push: Push): string {
    const template = pushTemplate[push.template];

    if (!template){
      this.logger.error(`Push template "${push.template}" does not exists`);
      return;
    }

    return JSON.stringify({
      notification: {
        title: this.replaceKeysInString(template.title, push.data),
        body: this.replaceKeysInString(template.body, push.data),
      },
    });
  }

  private replaceKeysInString(template: string, data: object): string {
    return Object.entries(data).reduce(
      (template, entry) => {
        const [key, value] = entry;
        return template.replace(`{{${key}}}`, value);
      },
      template,
    );
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
      subject: this.replaceKeysInString(emailSubject[email.template], email.data),
      template: email.template,
      context: {
        baseurl: process.env.BASEURL,
        ...email.data
      },
    });
  }
}
