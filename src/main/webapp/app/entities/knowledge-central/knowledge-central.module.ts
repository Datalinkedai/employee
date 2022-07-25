import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { KnowledgeCentralComponent } from './list/knowledge-central.component';
import { KnowledgeCentralDetailComponent } from './detail/knowledge-central-detail.component';
import { KnowledgeCentralUpdateComponent } from './update/knowledge-central-update.component';
import { KnowledgeCentralDeleteDialogComponent } from './delete/knowledge-central-delete-dialog.component';
import { KnowledgeCentralRoutingModule } from './route/knowledge-central-routing.module';

@NgModule({
  imports: [SharedModule, KnowledgeCentralRoutingModule],
  declarations: [
    KnowledgeCentralComponent,
    KnowledgeCentralDetailComponent,
    KnowledgeCentralUpdateComponent,
    KnowledgeCentralDeleteDialogComponent,
  ],
  entryComponents: [KnowledgeCentralDeleteDialogComponent],
})
export class KnowledgeCentralModule {}
