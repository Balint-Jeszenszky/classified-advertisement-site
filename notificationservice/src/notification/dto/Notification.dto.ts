
export class Email {
  toAddress: string;
  template: string;
  data: object;
}

export class Push {
  userId: number;
  template: string;
  data: object;
}

export class ServerSentEvent {

}

export class Notification {
  email?: Email;
  push?: Push;
  serverSentEvent?: ServerSentEvent;
}
