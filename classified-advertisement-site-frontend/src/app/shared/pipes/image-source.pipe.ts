import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'imageSource'
})
export class ImageSourcePipe implements PipeTransform {

  transform(value: File | Blob): Promise<string | ArrayBuffer | null> {
    return new Promise(r => {
      const reader = new FileReader();
      reader.addEventListener('loadend', () => {
        r(reader.result);
      });
      reader.readAsDataURL(value);
    });
  }

}
