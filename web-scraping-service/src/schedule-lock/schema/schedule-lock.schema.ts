import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type ScheduleLockDocument = HydratedDocument<ScheduleLock>;

@Schema()
export class ScheduleLock {
  @Prop({ required: true })
  process: string;

  @Prop({ required: true })
  task: string;

  @Prop({ type: Date, required: true })
  lockedAt: Date;

  @Prop({ type: Date, required: true })
  lockedUntil: Date;
}

export const ScheduleLockSchema = SchemaFactory.createForClass(ScheduleLock);
