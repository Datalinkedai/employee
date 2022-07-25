import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DocumentsComponent } from '../list/documents.component';
import { DocumentsDetailComponent } from '../detail/documents-detail.component';
import { DocumentsUpdateComponent } from '../update/documents-update.component';
import { DocumentsRoutingResolveService } from './documents-routing-resolve.service';

const documentsRoute: Routes = [
  {
    path: '',
    component: DocumentsComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DocumentsDetailComponent,
    resolve: {
      documents: DocumentsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DocumentsUpdateComponent,
    resolve: {
      documents: DocumentsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DocumentsUpdateComponent,
    resolve: {
      documents: DocumentsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(documentsRoute)],
  exports: [RouterModule],
})
export class DocumentsRoutingModule {}
