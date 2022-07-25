import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { KnowledgeCentralComponent } from '../list/knowledge-central.component';
import { KnowledgeCentralDetailComponent } from '../detail/knowledge-central-detail.component';
import { KnowledgeCentralUpdateComponent } from '../update/knowledge-central-update.component';
import { KnowledgeCentralRoutingResolveService } from './knowledge-central-routing-resolve.service';

const knowledgeCentralRoute: Routes = [
  {
    path: '',
    component: KnowledgeCentralComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: KnowledgeCentralDetailComponent,
    resolve: {
      knowledgeCentral: KnowledgeCentralRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: KnowledgeCentralUpdateComponent,
    resolve: {
      knowledgeCentral: KnowledgeCentralRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: KnowledgeCentralUpdateComponent,
    resolve: {
      knowledgeCentral: KnowledgeCentralRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(knowledgeCentralRoute)],
  exports: [RouterModule],
})
export class KnowledgeCentralRoutingModule {}
