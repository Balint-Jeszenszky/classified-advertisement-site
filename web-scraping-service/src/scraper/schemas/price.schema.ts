import { Prop, Schema, SchemaFactory } from "@nestjs/mongoose";
import { HydratedDocument } from "mongoose";


export type PriceDocument = HydratedDocument<Price>;

@Schema()
export class Price {
  @Prop({ required: true })
  advertisementId: number;

  @Prop({ required: true })
  categoryId: number;

  @Prop()
  site?: string;

  @Prop()
  image?: string;
  
  @Prop({ required: true })
  originalTitle: string;
  
  @Prop()
  title: string;
  
  @Prop()
  url?: string;
  
  @Prop()
  price?: number;
}

export const PriceSchema = SchemaFactory.createForClass(Price);
