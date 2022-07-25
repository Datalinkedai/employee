import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOptions, Options } from '../options.model';
import { OptionsService } from '../service/options.service';

@Injectable({ providedIn: 'root' })
export class OptionsRoutingResolveService implements Resolve<IOptions> {
  constructor(protected service: OptionsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOptions> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((options: HttpResponse<Options>) => {
          if (options.body) {
            return of(options.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Options());
  }
}
