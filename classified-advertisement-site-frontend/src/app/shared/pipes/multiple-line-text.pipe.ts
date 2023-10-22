import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'multipleLineText'
})
export class MultipleLineTextPipe implements PipeTransform {

  transform(value: string | undefined, ...args: unknown[]): string[] {
    return value?.split('\n') ?? [];
  }

}
