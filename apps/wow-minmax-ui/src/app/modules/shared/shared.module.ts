import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DropdownSelectComponent } from './components/dropdown-select/dropdown-select.component';
import { BlankZeroPipe } from './pipes/blank-zero.pipe';
import { IconPipe } from './pipes/icon.pipe';

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
		CommonModule,
		NgbModule,
		DropdownSelectComponent,
		IconPipe,
		BlankZeroPipe
	]
})
export class SharedModule { }
