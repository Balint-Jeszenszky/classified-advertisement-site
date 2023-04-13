import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'imageSource'
})
export class ImageSourcePipe implements PipeTransform {

  transform(value: File, ...args: unknown[]): Promise<string | ArrayBuffer | null> {
    return new Promise(r => {
      const reader = new FileReader();
      reader.addEventListener('loadend', e => {
        const res = reader.result;
        r(res);
      });
      reader.readAsDataURL(value);
    });
  }

}
