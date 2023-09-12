import { ApiProperty } from "@nestjs/swagger";

class PushSubscriptionKeys {
  @ApiProperty()
  p256dh: string;

  @ApiProperty()
  auth: string;
}

export class PushSubscriptionRequest {
  @ApiProperty()
  endpoint: string;

  @ApiProperty()
  expirationTime: string | null;

  @ApiProperty()
  keys: PushSubscriptionKeys;
}
