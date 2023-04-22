import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-gallery',
  templateUrl: './gallery.component.html',
  styleUrls: ['./gallery.component.scss']
})
export class GalleryComponent implements OnInit {
  @Input() imageUrls?: string[];
  currentImage?: string;

  ngOnInit(): void {
    this.currentImage = this.imageUrls?.at(0);
  }

  showImage(imageUrl: string) {
    this.currentImage = imageUrl;
  }
}
