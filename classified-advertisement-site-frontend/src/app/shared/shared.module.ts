import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LocaleDateTimePipe } from './pipes/locale-date-time.pipe';
import { MultipleLineTextPipe } from './pipes/multiple-line-text.pipe';



@NgModule({
  declarations: [
    LocaleDateTimePipe,
    MultipleLineTextPipe
  ],
  imports: [
    CommonModule
  ],
  exports: [
    LocaleDateTimePipe,
    MultipleLineTextPipe,
  ],
})
export class SharedModule { }
