import { Module } from '@nestjs/common';
import { APP_GUARD } from '@nestjs/core';
import { HeaderAuthGuard } from './auth/header-auth.guard';

@Module({
  imports: [],
  controllers: [],
  providers: [
    {
      provide: APP_GUARD,
      useClass: HeaderAuthGuard,
    },
  ],
})
export class AppModule {}
