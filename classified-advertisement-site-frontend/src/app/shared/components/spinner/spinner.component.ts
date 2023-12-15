import { Component, Input } from '@angular/core';

export enum LoadingState {
  LOADING,
  LOADED,
  ERROR,
}

@Component({
  selector: 'app-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.scss']
})
export class SpinnerComponent {
  @Input() loadingState: LoadingState = LoadingState.LOADING;
  loadingStates = LoadingState;
}
