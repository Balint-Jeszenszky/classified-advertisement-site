
import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

class PushSubscriptionKeys {
  @Prop({ required: true })
  p256dh: string;

  @Prop({ required: true })
  auth: string;
}

export class PushSubscriptionData {
  @Prop({ required: true, unique: true })
  endpoint: string;

  @Prop()
  expirationTime: string | null;

  @Prop({ required: true, type: PushSubscriptionKeys })
  keys: PushSubscriptionKeys;
}

@Schema()
export class PushSubscription {

  constructor(userId: number) {
    this.userId = userId;
  }

  @Prop({ required: true })
  userId: number;

  @Prop({ required: true, type: [typeof PushSubscriptionData] })
  subscriptions: PushSubscriptionData[] = [];
}

export type PushSubscriptionDocument = HydratedDocument<PushSubscription>;
export const PushSubscriptionSchema = SchemaFactory.createForClass(PushSubscription);
