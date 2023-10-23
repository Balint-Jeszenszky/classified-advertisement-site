import { Module } from '@nestjs/common';
import { ScheduleLockService } from './schedule-lock.service';
import { MongooseModule } from '@nestjs/mongoose';
import { ScheduleLock, ScheduleLockSchema } from './schema/schedule-lock.schema';

@Module({
  imports: [MongooseModule.forFeature([{ name: ScheduleLock.name, schema: ScheduleLockSchema }])],
  providers: [ScheduleLockService],
  exports: [ScheduleLockService],
})
export class ScheduleLockModule {}
