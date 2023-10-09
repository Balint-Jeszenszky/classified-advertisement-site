import { Inject, Injectable, Logger, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Socket } from 'socket.io';
import Chat from './entity/chat.entity';
import { Repository } from 'typeorm';
import Message from './entity/message.entity';
import { ClientProxy } from '@nestjs/microservices';

@Injectable()
export class ChatService {
  private readonly logger: Logger = new Logger(ChatService.name);

  private onlineUsers: Map<number, Socket> = new Map();

  constructor(
    @Inject('REALTIME_CHAT_SERVICE') private readonly client: ClientProxy,
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
    const chat = await this.chatRepository.findOne({
      where: [
        { id, fromUserId: currentUserId },
        { id, advertisementOwnerUserId: currentUserId }
      ],
      relations: {
        messages: true,
      },
     });

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

    this.publishMessageEvent(chat, message);

    return message;
  }

  async sendMessageToChat(chatId: number, fromUserId: number, text: string) {
    const chat = await this.chatRepository.findOne({ where: [{ id: chatId, fromUserId }, { id: chatId, advertisementOwnerUserId: fromUserId }] });

    if (!chat) {
      throw new NotFoundException();
    }

    const message = await this.messageRepository.save(new Message({ userId: fromUserId, text, chat }));

    this.publishMessageEvent(chat, message);

    return message;
  }

  sendMessageToClient(userId: number, message: Message) {
    const client = this.onlineUsers.get(userId);

    if (!client) {
      return;
    }

    client.emit('message', message);
  }

  addOnlineUser(client: Socket) {
    this.onlineUsers.set(client.data.user.id, client);
  }

  removeOnlineUser(client: Socket) {
    this.onlineUsers.delete(client.data.user.id);
  }

  private publishMessageEvent(chat: Chat, message: Message) {
    const payload = {
      userId: chat.fromUserId === message.userId ? chat.advertisementOwnerUserId : chat.fromUserId,
      message,
    };

    this.client.emit('message', payload);
  }
}
