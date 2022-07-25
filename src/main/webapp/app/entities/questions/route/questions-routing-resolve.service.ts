import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IQuestions, Questions } from '../questions.model';
import { QuestionsService } from '../service/questions.service';

@Injectable({ providedIn: 'root' })
export class QuestionsRoutingResolveService implements Resolve<IQuestions> {
  constructor(protected service: QuestionsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IQuestions> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((questions: HttpResponse<Questions>) => {
          if (questions.body) {
            return of(questions.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Questions());
  }
}
