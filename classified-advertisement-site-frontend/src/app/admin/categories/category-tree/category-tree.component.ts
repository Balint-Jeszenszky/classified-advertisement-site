import { FlatTreeControl } from '@angular/cdk/tree';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { Observable } from 'rxjs';
import { CategoryResponse } from 'src/app/openapi/advertisementservice';

interface CategoryNode {
  name: string;
  id: number;
  children?: CategoryNode[];
}

interface CategoryFlatNode {
  expandable: boolean;
  name: string;
  level: number;
  id: number;
}

@Component({
  selector: 'app-category-tree',
  templateUrl: './category-tree.component.html',
  styleUrls: ['./category-tree.component.scss']
})
export class CategoryTreeComponent implements OnInit {
  @Input() categories?: Observable<CategoryResponse[]>;
  @Output() selectCategoryEvent: EventEmitter<number> = new EventEmitter();
  treeControl = new FlatTreeControl<CategoryFlatNode>(
    node => node.level,
    node => node.expandable,
  );
  treeFlattener = new MatTreeFlattener(
    (node: CategoryNode, level: number) => ({
      expandable: !!node.children && node.children.length > 0,
      name: node.name,
      level: level,
      id: node.id,
    }),
    node => node.level,
    node => node.expandable,
    node => node.children,
  );
  dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

  ngOnInit(): void {
    this.categories?.subscribe(res => {
      this.dataSource.data = this.createCategoryTree(res);
      this.treeControl.expandAll();
    });
  }

  private createCategoryTree(categoryList: CategoryResponse[], parentId: number | null = null): CategoryNode[] {
    return categoryList.filter(c => c.parentCategoryId === parentId).map(c => ({
      name: c.name,
      id: c.id,
      children: this.createCategoryTree(categoryList, c.id),
    }));
  }

  hasChild = (_: number, node: CategoryFlatNode) => node.expandable;

  editCategory(id: number) {
    this.selectCategoryEvent.emit(id);
  }
}
