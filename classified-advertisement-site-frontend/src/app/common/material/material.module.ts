import { NgModule } from '@angular/core';
import { MatChipsModule } from '@angular/material/chips';

const material = [
  MatChipsModule,
];

@NgModule({
  imports: [material],
  exports: [material],
})
export class MaterialModule { }
