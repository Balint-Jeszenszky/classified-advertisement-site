import { ApiProperty } from "@nestjs/swagger";

export class ScrapeRequest {
  @ApiProperty()
  siteId: string;
}
