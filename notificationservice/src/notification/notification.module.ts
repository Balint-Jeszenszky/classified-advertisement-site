import { DynamicModule, Module } from '@nestjs/common';
import { MessageQueueController } from './message-queue.controller';
import { NotificationService } from './notification.service';
import { NotificationController } from './notification.controller';
import { MailerModule } from '@nestjs-modules/mailer';
import { HandlebarsAdapter } from '@nestjs-modules/mailer/dist/adapters/handlebars.adapter';

@Module({})
export class NotificationModule {
  static forRoot(): DynamicModule {
    return {
      imports: [
        MailerModule.forRoot({
          transport: {
            host: process.env.EMAIL_HOST,
            port: parseInt(process.env.EMAIL_PORT),
            tls: {
              ciphers: 'SSLv3',
            },
            secure: false,
            auth: {
              user: process.env.EMAIL_USER,
              pass: process.env.EMAIL_PASS,
            },
          },
          template: {
            dir: process.cwd() + '/templates/',
            adapter: new HandlebarsAdapter(),
            options: {
              strict: true,
            },
          },
        }),
      ],
      controllers: [MessageQueueController, NotificationController],
      providers: [NotificationService],
      module: NotificationModule,
    }
  }
}
