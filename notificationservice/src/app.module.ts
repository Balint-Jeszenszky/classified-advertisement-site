import { Module } from '@nestjs/common';
import { NotificationModule } from './notification/notification.module';
import { ConfigModule } from '@nestjs/config';
import { APP_GUARD } from '@nestjs/core';
import { HeaderAuthGuard } from './auth/header-auth.guard';

@Module({
  imports: [
    ConfigModule.forRoot({
      envFilePath: process.env.NODE_ENV ? `.${process.env.NODE_ENV}.env` : '.env',
      isGlobal: true,
    }),
    NotificationModule.forRoot(),
  ],
  controllers: [],
  providers: [
    {
      provide: APP_GUARD,
      useClass: HeaderAuthGuard,
    },
  ],
})
export class AppModule {}
