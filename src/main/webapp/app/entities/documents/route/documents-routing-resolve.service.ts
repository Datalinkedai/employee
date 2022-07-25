import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDocuments, Documents } from '../documents.model';
import { DocumentsService } from '../service/documents.service';

@Injectable({ providedIn: 'root' })
export class DocumentsRoutingResolveService implements Resolve<IDocuments> {
  constructor(protected service: DocumentsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDocuments> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((documents: HttpResponse<Documents>) => {
          if (documents.body) {
            return of(documents.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Documents());
  }
}
