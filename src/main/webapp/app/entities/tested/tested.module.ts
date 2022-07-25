import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TestedComponent } from './list/tested.component';
import { TestedDetailComponent } from './detail/tested-detail.component';
import { TestedUpdateComponent } from './update/tested-update.component';
import { TestedDeleteDialogComponent } from './delete/tested-delete-dialog.component';
import { TestedRoutingModule } from './route/tested-routing.module';

@NgModule({
  imports: [SharedModule, TestedRoutingModule],
  declarations: [TestedComponent, TestedDetailComponent, TestedUpdateComponent, TestedDeleteDialogComponent],
  entryComponents: [TestedDeleteDialogComponent],
})
export class TestedModule {}
