import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OptionsComponent } from '../list/options.component';
import { OptionsDetailComponent } from '../detail/options-detail.component';
import { OptionsUpdateComponent } from '../update/options-update.component';
import { OptionsRoutingResolveService } from './options-routing-resolve.service';

const optionsRoute: Routes = [
  {
    path: '',
    component: OptionsComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OptionsDetailComponent,
    resolve: {
      options: OptionsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OptionsUpdateComponent,
    resolve: {
      options: OptionsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OptionsUpdateComponent,
    resolve: {
      options: OptionsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(optionsRoute)],
  exports: [RouterModule],
})
export class OptionsRoutingModule {}
