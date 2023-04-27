import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';


type SelectorDocument = HydratedDocument<Selector>;

@Schema()
class Selector {
  @Prop({ required: true })
  selector: string;

  @Prop({ required: true })
  property: string;
}

const SelectorSchema = SchemaFactory.createForClass(Selector);


type RootSelectorDocument = HydratedDocument<RootSelector>;

@Schema()
class RootSelector {
  @Prop({ required: true })
  base: string;

  @Prop({ required: true, type: SelectorSchema })
  image: Record<string, SelectorDocument>;

  @Prop({ required: true, type: SelectorSchema })
  price: Record<string, SelectorDocument>;

  @Prop({ required: true, type: SelectorSchema })
  title: Record<string, SelectorDocument>;

  @Prop({ required: true, type: SelectorSchema })
  url: Record<string, SelectorDocument>;
}

const RootSelectorSchema = SchemaFactory.createForClass(RootSelector);


export type SiteDocument = HydratedDocument<Site>;

@Schema()
export class Site {
  @Prop({ required: true })
  name: string;

  @Prop({ required: true })
  url: string;

  @Prop({ required: true })
  categoryId: number;

  @Prop({ required: true, type: RootSelectorSchema })
  selector: Record<string, RootSelectorDocument>
}

export const SiteSchema = SchemaFactory.createForClass(Site);
