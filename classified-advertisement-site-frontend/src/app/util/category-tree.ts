import { CategoryResponse } from "../openapi/advertisementservice";

export interface CategoryTree {
  id: number;
  name: string;
  children?: CategoryTree[];
}

export function createCategoryTree(categoryList: CategoryResponse[], parentId: number | null = null): CategoryTree[] {
  return categoryList.filter(c => c.parentCategoryId === parentId).map(c => ({
    name: c.name,
    id: c.id,
    children: createCategoryTree(categoryList, c.id),
  }));
}
