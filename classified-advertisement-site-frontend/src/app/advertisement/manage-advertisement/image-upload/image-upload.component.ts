import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-image-upload',
  templateUrl: './image-upload.component.html',
  styleUrls: ['./image-upload.component.scss']
})
export class ImageUploadComponent {
  @Input() advertisementId?: number;
  @Input() files: File[] = [];
  @Input() images: string[] = [];
  @Input() imagesToDelete: string[] = [];
  @Output() back: EventEmitter<File[]> = new EventEmitter();
  @Output() next: EventEmitter<File[]> = new EventEmitter();
  acceptedExtensions = ['.jpeg','.jpg','.png'];

  onFileSelected(event: Event) {
    const files = (<HTMLInputElement>event.target).files;
    const fileList: File[] = [];
    if (files) {
      for (let i = 0; i < files.length; i++) {
        if (this.acceptedExtensions.includes(`.${files.item(i)?.name.split('.').pop()?.toLocaleLowerCase()}`)) {
          fileList.push(files.item(i)!);
        }
      }
      this.files = [...this.files, ...fileList];
    }
  }

  removeFile(file: File) {
    this.files = this.files.filter(e => e !== file);
  }

  removeImage(image: string) {
    if (confirm('Delete image?')) {
      this.images = this.images.filter(i => i !== image);
      this.imagesToDelete.push(image);
    }
  }

  onBack() {
    this.back.emit(this.files);
  }

  onNext() {
    this.next.emit(this.files);
  }
}
