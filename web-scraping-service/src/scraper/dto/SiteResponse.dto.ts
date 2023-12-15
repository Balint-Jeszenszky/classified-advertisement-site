import { ApiProperty } from "@nestjs/swagger";
import { RootSelector } from "./SiteRequest.dto";

export class SiteResponse {
  @ApiProperty()
  id: string;
  
  @ApiProperty({ isArray: true, type: Number })
  categoryIds: number[];
  
  @ApiProperty()
  name: string;
  
  @ApiProperty()
  url: string;
  
  @ApiProperty()
  selector: RootSelector;
}
