import { AccountDetails } from "./auth.entity";

export interface Notification {
  id: string;
  title: string;
  description: string;
  receiver: AccountDetails;
}
