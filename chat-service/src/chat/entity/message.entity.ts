import { Column, CreateDateColumn, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';
import Chat from './chat.entity';

@Entity()
export default class Message {

  constructor(message?: Partial<Message>) {
    this.id = message?.id;
    this.text = message?.text;
    this.userId = message?.userId;
    this.createdAt = message?.createdAt;
    this.chat = message?.chat;
  }
  
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  text: string;

  @Column()
  userId: number;

  @CreateDateColumn()
  createdAt: Date;

  @ManyToOne(() => Chat, chat => chat.id)
  chat: Chat;
}
