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

  async getChatsForUser(userId: number) {
    const chats = await this.chatRepository.find({ where: [{ advertisementOwnerUserId: userId }, { fromUserId: userId }] });

    return chats;
  }

  async getChatById(id: number, currentUserId: number): Promise<Chat> {
    const chat = await this.chatRepository.findOne({ where: [{ id, fromUserId: currentUserId }, { id, advertisementOwnerUserId: currentUserId }] });

    if (!chat) {
      throw new NotFoundException();
    }

    return chat;
  }

  async getMessagesByChatId(id: number, currentUserId: number) {
    // TODO eager loading disabled
    const chat = await this.chatRepository.findOne({ where: [{ id, fromUserId: currentUserId }, { id, advertisementOwnerUserId: currentUserId }] });

    if (!chat) {
      throw new NotFoundException();
    }

    return chat.messages;
  }

  async sendMessageForAdvertisement(advertisementId: number, fromUserId: number, text: string) {
    let chat = await this.chatRepository.findOne({ where: [{ advertisementId, fromUserId }, { advertisementId, advertisementOwnerUserId: fromUserId }] });

    if (!chat) {
      // TODO if chat not exists check with advertisement microservice ant then create it
      chat = await this.chatRepository.save(new Chat({
        advertisementId,
        fromUserId,
        advertisementOwnerUserId: 1, // TODO read from advertisement microservice response
        messages: [],
      }));
    }

    const message = await this.messageRepository.save(new Message({ userId: fromUserId, text, chat }));

    return message;
  }

  async sendMessageToChat(chatId: number, fromUserId: number, text: string) {
    // TODO if chat not exists check with advertisement microservice ant then create it

    const chat = await this.chatRepository.findOne({ where: [{ id: chatId, fromUserId }, { id: chatId, advertisementOwnerUserId: fromUserId }] });

    if (!chat) {
      throw new NotFoundException();
    }

    const message = await this.messageRepository.save(new Message({ userId: fromUserId, text, chat }));

    return message;
  }
}
