import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';


class Selector {
  @Prop({ required: true })
  selector: string;

  @Prop({ required: true })
  property: string;
}


class RootSelector {
  @Prop({ required: true })
  base: string;

  @Prop({ required: true, type: Selector })
  image: Selector;

  @Prop({ required: true, type: Selector })
  price: Selector;

  @Prop({ required: true, type: Selector })
  title: Selector;

  @Prop({ required: true, type: Selector })
  url: Selector;
}


export type SiteDocument = HydratedDocument<Site>;

@Schema()
export class Site {
  @Prop({ required: true })
  name: string;

  @Prop({ required: true })
  url: string;

  @Prop({ required: true, type: [Number] })
  categoryIds: number[] = [];

  @Prop({ required: true, type: RootSelector })
  selector: RootSelector;
}

export const SiteSchema = SchemaFactory.createForClass(Site);
