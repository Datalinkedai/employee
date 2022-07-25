import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { KnowledgeComponent } from '../list/knowledge.component';
import { KnowledgeDetailComponent } from '../detail/knowledge-detail.component';
import { KnowledgeUpdateComponent } from '../update/knowledge-update.component';
import { KnowledgeRoutingResolveService } from './knowledge-routing-resolve.service';

const knowledgeRoute: Routes = [
  {
    path: '',
    component: KnowledgeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: KnowledgeDetailComponent,
    resolve: {
      knowledge: KnowledgeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: KnowledgeUpdateComponent,
    resolve: {
      knowledge: KnowledgeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: KnowledgeUpdateComponent,
    resolve: {
      knowledge: KnowledgeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(knowledgeRoute)],
  exports: [RouterModule],
})
export class KnowledgeRoutingModule {}
