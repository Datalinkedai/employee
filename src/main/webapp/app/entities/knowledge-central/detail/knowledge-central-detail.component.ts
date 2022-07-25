import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IKnowledgeCentral } from '../knowledge-central.model';

@Component({
  selector: 'jhi-knowledge-central-detail',
  templateUrl: './knowledge-central-detail.component.html',
})
export class KnowledgeCentralDetailComponent implements OnInit {
  knowledgeCentral: IKnowledgeCentral | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ knowledgeCentral }) => {
      this.knowledgeCentral = knowledgeCentral;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
