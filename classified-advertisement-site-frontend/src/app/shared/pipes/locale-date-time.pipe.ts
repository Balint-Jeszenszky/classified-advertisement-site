import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'localeDateTime'
})
export class LocaleDateTimePipe implements PipeTransform {

  transform(value: string | number, ...args: unknown[]): unknown {
    return new Date(value).toLocaleString([], { dateStyle: 'long', timeStyle: 'short' });
  }

}
