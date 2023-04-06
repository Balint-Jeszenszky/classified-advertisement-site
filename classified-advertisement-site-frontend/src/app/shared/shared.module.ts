import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LocaleDateTimePipe } from './pipes/locale-date-time.pipe';



@NgModule({
  declarations: [
    LocaleDateTimePipe
  ],
  imports: [
    CommonModule
  ],
  exports: [
    LocaleDateTimePipe,
  ],
})
export class SharedModule { }
