import { ApiProperty } from "@nestjs/swagger";

export class PublicVapidKeyResponse {

  constructor(publicVapidKey: string) {
    this.publicVapidKey = publicVapidKey;
  }

  @ApiProperty()
  publicVapidKey: string;
}
