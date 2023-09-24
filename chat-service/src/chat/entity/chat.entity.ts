import { Column, Entity, Index, OneToMany, PrimaryGeneratedColumn } from 'typeorm';
import Message from './message.entity';

@Entity()
@Index(['advertisementId', 'fromUserId'], { unique: true })
export default class Chat {

  constructor(chat?: Partial<Chat>) {
    this.id = chat?.id;
    this.advertisementId = chat?.advertisementId;
    this.advertisementOwnerUserId = chat?.advertisementOwnerUserId;
    this.fromUserId = chat?.fromUserId;
    this.messages = chat?.messages;
  }

  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  advertisementId: number;

  @Column()
  advertisementOwnerUserId: number;

  @Column()
  fromUserId: number;

  @OneToMany(() => Message, message => message.chat)
  messages: Message[];
}
