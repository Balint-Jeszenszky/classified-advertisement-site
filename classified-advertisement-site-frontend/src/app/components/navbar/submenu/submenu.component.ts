import { Component, Input } from '@angular/core';
import { MatMenuTrigger } from '@angular/material/menu';
import { CategoryTree } from 'src/app/util/category-tree';

@Component({
  selector: 'app-submenu',
  templateUrl: './submenu.component.html',
  styleUrls: ['./submenu.component.scss']
})
export class SubmenuComponent {
  @Input() path?: string;
  @Input() tree?: CategoryTree[];
  private openedMenuTrigger?: MatMenuTrigger

  openMenu(menuTrigger: MatMenuTrigger) {
    if (this.openedMenuTrigger === menuTrigger) {
      return;
    }
    this.openedMenuTrigger?.closeMenu();
    this.openedMenuTrigger = menuTrigger;
    menuTrigger.openMenu();
  }

  menuClosed() {
    this.openedMenuTrigger = undefined;
  }
}
