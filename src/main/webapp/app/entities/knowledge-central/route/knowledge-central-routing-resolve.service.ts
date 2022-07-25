import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IKnowledgeCentral, KnowledgeCentral } from '../knowledge-central.model';
import { KnowledgeCentralService } from '../service/knowledge-central.service';

@Injectable({ providedIn: 'root' })
export class KnowledgeCentralRoutingResolveService implements Resolve<IKnowledgeCentral> {
  constructor(protected service: KnowledgeCentralService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IKnowledgeCentral> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((knowledgeCentral: HttpResponse<KnowledgeCentral>) => {
          if (knowledgeCentral.body) {
            return of(knowledgeCentral.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new KnowledgeCentral());
  }
}
