import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { KnowledgeComponent } from './list/knowledge.component';
import { KnowledgeDetailComponent } from './detail/knowledge-detail.component';
import { KnowledgeUpdateComponent } from './update/knowledge-update.component';
import { KnowledgeDeleteDialogComponent } from './delete/knowledge-delete-dialog.component';
import { KnowledgeRoutingModule } from './route/knowledge-routing.module';

@NgModule({
  imports: [SharedModule, KnowledgeRoutingModule],
  declarations: [KnowledgeComponent, KnowledgeDetailComponent, KnowledgeUpdateComponent, KnowledgeDeleteDialogComponent],
  entryComponents: [KnowledgeDeleteDialogComponent],
})
export class KnowledgeModule {}
