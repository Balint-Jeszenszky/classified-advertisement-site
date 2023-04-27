import { ApiProperty } from "@nestjs/swagger";

export class ProductResponse {
  @ApiProperty()
  site?: string;
  
  @ApiProperty()
  image?: string;

  @ApiProperty()
  title: string;

  @ApiProperty()
  url?: string;

  @ApiProperty()
  price?: number;
}
