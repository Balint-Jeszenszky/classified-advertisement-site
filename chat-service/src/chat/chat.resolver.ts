import { Args, Int, Mutation, Parent, Query, ResolveField, Resolver } from '@nestjs/graphql';
import { ChatService } from './chat.service';
import { Chat, Message, NewAdvertisementMessage, NewChatMessage } from '../graphql/graphql';

@Resolver(of => Chat)
export class ChatResolver {

  constructor(
    private readonly chatService: ChatService,
  ) { }

  @Query(returns => Chat)
  chat(@Args('id', { type: () => Int }) id: number): Promise<Chat> {
    return this.chatService.getChatById(id, 1 /* TODO */) as any;
  }

  @ResolveField(returns => [Message])
  messages(@Parent() chat: Chat): Promise<Message[]> {
    return this.chatService.getMessagesByChatId(chat.id, 1 /* TODO */);
  }

  @Query(returns => Chat)
  chatsForUser(): Promise<Chat> {
    return this.chatService.getChatsForUser(1 /* TODO */) as any;
  }

  @Mutation(returns => Message)
  async sendMessageToChat(@Args({ name: 'newChatMessage', type: () => NewChatMessage }) newMessage: NewChatMessage): Promise<Message> {
    return this.chatService.sendMessageToChat(newMessage.chatId, 1 /* TODO */, newMessage.text);
  }

  @Mutation(returns => Message)
  async sendMessageForAdvertisement(@Args({ name: 'newAdvertisementMessage', type: () => NewAdvertisementMessage }) newMessage: NewAdvertisementMessage): Promise<Message> {
    return this.chatService.sendMessageForAdvertisement(newMessage.advertisementId, 1 /* TODO */, newMessage.text);
  }
}
