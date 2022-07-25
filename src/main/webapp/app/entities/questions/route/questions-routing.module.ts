import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { QuestionsComponent } from '../list/questions.component';
import { QuestionsDetailComponent } from '../detail/questions-detail.component';
import { QuestionsUpdateComponent } from '../update/questions-update.component';
import { QuestionsRoutingResolveService } from './questions-routing-resolve.service';

const questionsRoute: Routes = [
  {
    path: '',
    component: QuestionsComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: QuestionsDetailComponent,
    resolve: {
      questions: QuestionsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: QuestionsUpdateComponent,
    resolve: {
      questions: QuestionsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: QuestionsUpdateComponent,
    resolve: {
      questions: QuestionsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(questionsRoute)],
  exports: [RouterModule],
})
export class QuestionsRoutingModule {}
