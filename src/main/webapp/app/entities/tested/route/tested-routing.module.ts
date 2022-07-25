import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TestedComponent } from '../list/tested.component';
import { TestedDetailComponent } from '../detail/tested-detail.component';
import { TestedUpdateComponent } from '../update/tested-update.component';
import { TestedRoutingResolveService } from './tested-routing-resolve.service';

const testedRoute: Routes = [
  {
    path: '',
    component: TestedComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TestedDetailComponent,
    resolve: {
      tested: TestedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TestedUpdateComponent,
    resolve: {
      tested: TestedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TestedUpdateComponent,
    resolve: {
      tested: TestedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(testedRoute)],
  exports: [RouterModule],
})
export class TestedRoutingModule {}
