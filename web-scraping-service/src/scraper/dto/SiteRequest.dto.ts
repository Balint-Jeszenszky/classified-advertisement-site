import { ApiProperty } from "@nestjs/swagger";
import { Type } from "class-transformer";
import { IsNotEmpty, IsInt, ValidateNested } from "class-validator";

export class Selector {
  @ApiProperty()
  @IsNotEmpty()
  selector: string;

  @ApiProperty()
  @IsNotEmpty()
  property: string;
}

export class RootSelector {
  @ApiProperty()
  @IsNotEmpty()
  base: string;
  
  @ApiProperty()
  @ValidateNested({ each: true })
  @Type(() => Selector)
  image: Selector;
  
  @ApiProperty()
  @ValidateNested({ each: true })
  @Type(() => Selector)
  price: Selector;
  
  @ApiProperty()
  @ValidateNested({ each: true })
  @Type(() => Selector)
  title: Selector;
  
  @ApiProperty()
  @ValidateNested({ each: true })
  @Type(() => Selector)
  url: Selector;
}

export class SiteRequest {
  @ApiProperty()
  @IsInt()
  categoryId: number;

  @ApiProperty()
  @IsNotEmpty()
  name: string;

  @ApiProperty()
  @IsNotEmpty()
  url: string;
  
  @ApiProperty()
  @ValidateNested({ each: true })
  @Type(() => Selector)
  selector: RootSelector;
}
