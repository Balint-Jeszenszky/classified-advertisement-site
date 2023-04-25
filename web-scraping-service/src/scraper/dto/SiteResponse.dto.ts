import { RootSelector } from "./SiteRequest.dto";

export class SiteResponse {
  id: string;
  categoryId: number;
  name: string;
  url: string;
  rootSelector: RootSelector;
}
