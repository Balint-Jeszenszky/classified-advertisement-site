import { Prop, Schema, SchemaFactory } from "@nestjs/mongoose";
import { HydratedDocument } from "mongoose";


export type ProductDocument = HydratedDocument<Product>;

@Schema()
export class Product {
  @Prop({ required: true })
  advertisementId: number;

  @Prop({ required: true })
  categoryId: number;

  @Prop()
  site?: string;

  @Prop()
  image?: string;
  
  @Prop({ required: true })
  title: string;
  
  @Prop()
  productTitle?: string;
  
  @Prop()
  url?: string;
  
  @Prop()
  price?: number;
}

export const ProductSchema = SchemaFactory.createForClass(Product);
