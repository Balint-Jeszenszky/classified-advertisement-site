import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LocaleDateTimePipe } from './pipes/locale-date-time.pipe';
import { MultipleLineTextPipe } from './pipes/multiple-line-text.pipe';
import { ImageSourcePipe } from './pipes/image-source.pipe';



@NgModule({
  declarations: [
    LocaleDateTimePipe,
    MultipleLineTextPipe,
    ImageSourcePipe
  ],
  imports: [
    CommonModule
  ],
  exports: [
    LocaleDateTimePipe,
    MultipleLineTextPipe,
    ImageSourcePipe
  ],
})
export class SharedModule { }
