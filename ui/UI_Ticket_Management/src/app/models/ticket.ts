import { User } from './user';

export interface Ticket {
  id?: number;
  title: string;
  description: string;
  documentSummary?: string;
  documentName?: string;
  createdBy?: User;
  assignedTo?: User;
  createdAt?: string;
  updatedAt?: string;
}
