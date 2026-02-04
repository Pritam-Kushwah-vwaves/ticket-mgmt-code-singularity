import { Permission } from "../components/login/permission.enum";

export enum Role {
  ADMIN = 'ADMIN',
  GENERAL = 'GENERAL'
}


export interface User {
  id?: number;
  username: string;
  password?: string; // Optional since we don't always send password
  role?: Role;
  permissions?: Permission[];
}
