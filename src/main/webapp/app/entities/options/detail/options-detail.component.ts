import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOptions } from '../options.model';

@Component({
  selector: 'jhi-options-detail',
  templateUrl: './options-detail.component.html',
})
export class OptionsDetailComponent implements OnInit {
  options: IOptions | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ options }) => {
      this.options = options;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
