import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITested } from '../tested.model';

@Component({
  selector: 'jhi-tested-detail',
  templateUrl: './tested-detail.component.html',
})
export class TestedDetailComponent implements OnInit {
  tested: ITested | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tested }) => {
      this.tested = tested;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
