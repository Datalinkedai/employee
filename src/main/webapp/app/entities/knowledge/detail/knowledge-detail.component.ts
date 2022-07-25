import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IKnowledge } from '../knowledge.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-knowledge-detail',
  templateUrl: './knowledge-detail.component.html',
})
export class KnowledgeDetailComponent implements OnInit {
  knowledge: IKnowledge | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ knowledge }) => {
      this.knowledge = knowledge;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
