import { Prop, Schema, SchemaFactory } from "@nestjs/mongoose";
import { HydratedDocument } from "mongoose";


export type PriceDocument = HydratedDocument<Price>;

@Schema()
export class Price {
  @Prop({ required: true })
  advertisementId: number;

  @Prop({ required: true })
  site: string;

  @Prop({ required: true })
  image: string;
  
  @Prop({ required: true })
  title: string;
  
  @Prop({ required: true })
  url: string;
  
  @Prop({ required: true })
  price: number;
}

export const PriceSchema = SchemaFactory.createForClass(Price);
