
export enum MessageType {
  CREATE = 'CREATE',
  UPDATE = 'UPDATE',
  DELETE = 'DELETE',
}

export class Advertisement {
  id: number;
  type: MessageType;
  title: string;
  categoryId: number;
}
