import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'multipleLineText'
})
export class MultipleLineTextPipe implements PipeTransform {

  transform(value: string, ...args: unknown[]): string[] {
    return value.split('\n');
  }

}
