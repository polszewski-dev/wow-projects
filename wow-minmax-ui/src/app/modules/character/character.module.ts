import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';
import { BuffEditorComponent } from './components/buff-editor/buff-editor.component';
import { EquipmentEditorComponent } from './components/equipment-editor/equipment-editor.component';
import { EquipmentSlotEditorComponent } from './components/equipment-slot-editor/equipment-slot-editor.component';
import { UpgradeListComponent } from './components/upgrade-list/upgrade-list.component';
import { ItemSelectComponent } from './components/item-select/item-select.component';
import { GemSelectComponent } from './components/gem-select/gem-select.component';
import { EnchantSelectComponent } from './components/enchant-select/enchant-select.component';
import { SocketStatusComponent } from './components/socket-status/socket-status.component';
import { EquipUpgradeButtonComponent } from './components/equip-upgrade-button/equip-upgrade-button.component';

@NgModule({
	declarations: [
		BuffEditorComponent,
		EquipmentEditorComponent,
		EquipmentSlotEditorComponent,
		ItemSelectComponent,
        EnchantSelectComponent,
        GemSelectComponent,
        SocketStatusComponent,
		EquipUpgradeButtonComponent,
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
		ItemSelectComponent,
        EnchantSelectComponent,
        GemSelectComponent,
        SocketStatusComponent,
		EquipUpgradeButtonComponent,
		UpgradeListComponent
	]
})
export class CharacterModule { }
