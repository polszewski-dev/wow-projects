import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';
import { BuffEditorComponent } from './components/buff-editor/buff-editor.component';
import { EquipmentEditorComponent } from './components/equipment-editor/equipment-editor.component';
import { EquipmentSlotEditorComponent } from './components/equipment-slot-editor/equipment-slot-editor.component';
import { UpgradeListComponent } from './components/upgrade-list/upgrade-list.component';

@NgModule({
	declarations: [
		BuffEditorComponent,
		EquipmentEditorComponent,
		EquipmentSlotEditorComponent,
		UpgradeListComponent
	],
	imports: [
		CommonModule,
		FormsModule,
		SharedModule
	],
	exports: [
		BuffEditorComponent,
		EquipmentEditorComponent,
		EquipmentSlotEditorComponent,
		UpgradeListComponent
	]
})
export class CharacterModule { }
