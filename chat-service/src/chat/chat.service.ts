import { Injectable, Logger, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import Chat from './entity/chat.entity';
import { Repository } from 'typeorm';
import Message from './entity/message.entity';

@Injectable()
export class ChatService {
  private readonly logger: Logger = new Logger(ChatService.name);

  constructor(
    @InjectRepository(Chat) private readonly chatRepository: Repository<Chat>,
    @InjectRepository(Message) private readonly messageRepository: Repository<Message>,
  ) { }

  async findChatsForUser(userId: number) {
    const chats = await this.chatRepository.find({ where: [{ advertisementOwnerUserId: userId }, { fromUserId: userId }] });

    return chats;
  }

  async getMessagesByChatId(id: number, currentUserId: number) {
    const chat = await this.chatRepository.findOne({ where: [{ id, fromUserId: currentUserId }, { id, advertisementOwnerUserId: currentUserId }] });

    if (!chat) {
      throw new NotFoundException();
    }

    return chat.messages;
  }

  async sendMessageForAdvertisement(advertisementId: number, fromUserId: number, text: string) {
    // TODO if chat not exists check with advertisement microservice ant then create it

    const chat = await this.chatRepository.findOne({ where: [{ advertisementId, fromUserId }, { advertisementId, advertisementOwnerUserId: fromUserId }] });

    if (!chat) {
      throw new NotFoundException();
    }

    const message = await this.messageRepository.save(new Message({ userId: fromUserId, text, chat }));

    return message;
  }
}
