import { Component, OnInit } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-manage-advertisement',
  templateUrl: './manage-advertisement.component.html',
  styleUrls: ['./manage-advertisement.component.scss']
})
export class ManageAdvertisementComponent implements OnInit {
  advertisementId?: number;
  newAdvertisement: boolean = true;

  constructor(
    private readonly route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.advertisementId = +params['id'];
        this.newAdvertisement = false;
      }
    });
  }

  nextStep(stepper: MatStepper, advertisementId?: number) {
    if (advertisementId) {
      this.advertisementId = advertisementId;
    }

    stepper.next();
  }
}
