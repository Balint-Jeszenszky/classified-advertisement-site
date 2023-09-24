import { Injectable, Logger } from '@nestjs/common';

@Injectable()
export class ChatService {
  private readonly logger: Logger = new Logger(ChatService.name);

  constructor(

  ) { }

  findChatsForUser(userId: number) {

  }

  getMessagesByChatId(chatId: number) {

  }

  sendMessageForAdvertisement(advertisementId: number, fromUserId: number) {

  }
}
