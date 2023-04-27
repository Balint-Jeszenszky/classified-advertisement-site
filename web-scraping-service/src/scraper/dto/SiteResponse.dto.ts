import { ApiProperty } from "@nestjs/swagger";
import { RootSelector } from "./SiteRequest.dto";

export class SiteResponse {
  @ApiProperty()
  id: string;
  
  @ApiProperty()
  categoryId: number;
  
  @ApiProperty()
  name: string;
  
  @ApiProperty()
  url: string;
  
  @ApiProperty()
  selector: RootSelector;
}
