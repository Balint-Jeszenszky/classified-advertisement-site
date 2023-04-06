import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent {
  @Input() term: string = '';
  @Output() searchEvent: EventEmitter<string> = new EventEmitter();

  search() {
    this.searchEvent.emit(this.term);
  }
}
