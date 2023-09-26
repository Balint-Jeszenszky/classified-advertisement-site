import { Args, Int, Mutation, Parent, Query, ResolveField, Resolver } from '@nestjs/graphql';
import { ChatService } from './chat.service';
import { Chat, Message, NewAdvertisementMessage, NewChatMessage } from '../graphql/graphql';
import { CurrentUser } from 'src/auth/current-user.decorator';
import { User } from 'src/auth/User.model';

@Resolver(of => Chat)
export class ChatResolver {

  constructor(
    private readonly chatService: ChatService,
  ) { }

  @Query(returns => Chat)
  chat(
    @Args('id', { type: () => Int }) id: number,
    @CurrentUser() user: User,
  ): Promise<Chat> {
    return this.chatService.getChatById(id, user.id) as any;
  }

  @ResolveField(returns => [Message])
  messages(
    @Parent() chat: Chat,
    @CurrentUser() user: User,
  ): Promise<Message[]> {
    return this.chatService.getMessagesByChatId(chat.id, user.id);
  }

  @Query(returns => Chat)
  chatsForUser(@CurrentUser() user: User): Promise<Chat> {
    return this.chatService.getChatsForUser(user.id) as any;
  }

  @Mutation(returns => Message)
  async sendMessageToChat(
    @Args({ name: 'newChatMessage', type: () => NewChatMessage }) newMessage: NewChatMessage,
    @CurrentUser() user: User,
  ): Promise<Message> {
    return this.chatService.sendMessageToChat(newMessage.chatId, user.id, newMessage.text);
  }

  @Mutation(returns => Message)
  async sendMessageForAdvertisement(
    @Args({ name: 'newAdvertisementMessage', type: () => NewAdvertisementMessage }) newMessage: NewAdvertisementMessage,
    @CurrentUser() user: User,
  ): Promise<Message> {
    return this.chatService.sendMessageForAdvertisement(newMessage.advertisementId, user.id, newMessage.text);
  }
}
