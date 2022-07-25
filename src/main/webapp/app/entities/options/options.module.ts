import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OptionsComponent } from './list/options.component';
import { OptionsDetailComponent } from './detail/options-detail.component';
import { OptionsUpdateComponent } from './update/options-update.component';
import { OptionsDeleteDialogComponent } from './delete/options-delete-dialog.component';
import { OptionsRoutingModule } from './route/options-routing.module';

@NgModule({
  imports: [SharedModule, OptionsRoutingModule],
  declarations: [OptionsComponent, OptionsDetailComponent, OptionsUpdateComponent, OptionsDeleteDialogComponent],
  entryComponents: [OptionsDeleteDialogComponent],
})
export class OptionsModule {}
