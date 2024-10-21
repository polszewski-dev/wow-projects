import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DropdownSelectComponent } from './components/dropdown-select/dropdown-select.component';
import { IconPipe } from './pipes/icon.pipe';
import { BlankZeroPipe } from './pipes/blank-zero.pipe';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

@NgModule({
	declarations: [
		DropdownSelectComponent,
		IconPipe,
		BlankZeroPipe
	],
	imports: [
		CommonModule,
		NgbModule
	],
	exports: [
		DropdownSelectComponent,
		IconPipe,
		BlankZeroPipe
	]
})
export class SharedModule { }
