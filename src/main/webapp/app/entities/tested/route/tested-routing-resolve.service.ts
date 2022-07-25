import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITested, Tested } from '../tested.model';
import { TestedService } from '../service/tested.service';

@Injectable({ providedIn: 'root' })
export class TestedRoutingResolveService implements Resolve<ITested> {
  constructor(protected service: TestedService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITested> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tested: HttpResponse<Tested>) => {
          if (tested.body) {
            return of(tested.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Tested());
  }
}
