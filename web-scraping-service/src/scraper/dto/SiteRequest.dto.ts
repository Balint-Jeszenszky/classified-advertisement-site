import { Type } from "class-transformer";
import { IsNotEmpty, IsNumber, ValidateNested } from "class-validator";

export class Selector {
  @IsNotEmpty()
  selector: string;

  @IsNotEmpty()
  property: string;
}

export class RootSelector {
  @IsNotEmpty()
  base: string;
  
  @ValidateNested({ each: true })
  @Type(() => Selector)
  image: Selector;
  
  @ValidateNested({ each: true })
  @Type(() => Selector)
  price: Selector;
  
  @ValidateNested({ each: true })
  @Type(() => Selector)
  title: Selector;
  
  @ValidateNested({ each: true })
  @Type(() => Selector)
  url: Selector;
}

export class SiteRequest {
  @IsNumber()
  categoryId: number;

  @IsNotEmpty()
  name: string;

  @IsNotEmpty()
  url: string;
  
  @ValidateNested({ each: true })
  @Type(() => Selector)
  selector: RootSelector;
}
