export class SiteRequest {
  categoryId: number;
  name: string;
  url: string;
  rootSelector: RootSelector;
}

export class RootSelector {
  base: string;
  image: Selector;
  price: Selector;
  title: Selector;
  url: Selector;
}

export class Selector {
  selector: string;
  property: string;
}