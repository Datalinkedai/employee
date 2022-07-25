import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { QuestionsComponent } from './list/questions.component';
import { QuestionsDetailComponent } from './detail/questions-detail.component';
import { QuestionsUpdateComponent } from './update/questions-update.component';
import { QuestionsDeleteDialogComponent } from './delete/questions-delete-dialog.component';
import { QuestionsRoutingModule } from './route/questions-routing.module';

@NgModule({
  imports: [SharedModule, QuestionsRoutingModule],
  declarations: [QuestionsComponent, QuestionsDetailComponent, QuestionsUpdateComponent, QuestionsDeleteDialogComponent],
  entryComponents: [QuestionsDeleteDialogComponent],
})
export class QuestionsModule {}
