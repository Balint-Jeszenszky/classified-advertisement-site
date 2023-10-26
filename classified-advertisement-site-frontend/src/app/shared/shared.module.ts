import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LocaleDateTimePipe } from './pipes/locale-date-time.pipe';
import { MultipleLineTextPipe } from './pipes/multiple-line-text.pipe';
import { ImageSourcePipe } from './pipes/image-source.pipe';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@NgModule({
  declarations: [
    LocaleDateTimePipe,
    MultipleLineTextPipe,
    ImageSourcePipe,
    SpinnerComponent
  ],
  imports: [
    CommonModule,
    MatProgressSpinnerModule,
  ],
  exports: [
    LocaleDateTimePipe,
    MultipleLineTextPipe,
    ImageSourcePipe,
    SpinnerComponent,
  ],
})
export class SharedModule { }
