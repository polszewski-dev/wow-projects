import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';
import { BuffEditorComponent } from './components/buff-editor/buff-editor.component';
import { CharacterDetailsComponent } from './components/character-details/character-details.component';
import { ConsumableEditorComponent } from './components/consumable-editor/consumable-editor.component';
import { EnchantSelectComponent } from './components/enchant-select/enchant-select.component';
import { EquipUpgradeButtonComponent } from './components/equip-upgrade-button/equip-upgrade-button.component';
import { EquipmentEditorComponent } from './components/equipment-editor/equipment-editor.component';
import { EquipmentSlotEditorComponent } from './components/equipment-slot-editor/equipment-slot-editor.component';
import { ExclusiveFactionSelectComponent } from './components/exclusive-faction-select/exclusive-faction-select.component';
import { GemSelectComponent } from './components/gem-select/gem-select.component';
import { ItemFilterComponent } from './components/item-filter/item-filter.component';
import { ItemSelectComponent } from './components/item-select/item-select.component';
import { ProfessionSelectComponent } from './components/profession-select/profession-select.component';
import { ScriptSelectComponent } from './components/script-select/script-select.component';
import { SocketStatusComponent } from './components/socket-status/socket-status.component';
import { UpgradeListComponent } from './components/upgrade-list/upgrade-list.component';

@NgModule({
	declarations: [
		BuffEditorComponent,
		ConsumableEditorComponent,
		EquipmentEditorComponent,
		EquipmentSlotEditorComponent,
		ItemSelectComponent,
        EnchantSelectComponent,
        GemSelectComponent,
        SocketStatusComponent,
		EquipUpgradeButtonComponent,
		ItemFilterComponent,
		UpgradeListComponent,
		CharacterDetailsComponent,
		ProfessionSelectComponent,
		ExclusiveFactionSelectComponent,
		ScriptSelectComponent,
	],
	imports: [
		FormsModule,
		ReactiveFormsModule,
		SharedModule
	],
	exports: [
		FormsModule,
		ReactiveFormsModule,
		SharedModule,
		BuffEditorComponent,
		ConsumableEditorComponent,
		EquipmentEditorComponent,
		EquipmentSlotEditorComponent,
		ItemSelectComponent,
        EnchantSelectComponent,
        GemSelectComponent,
        SocketStatusComponent,
		EquipUpgradeButtonComponent,
		UpgradeListComponent,
		CharacterDetailsComponent,
		ProfessionSelectComponent,
		ExclusiveFactionSelectComponent,
		ScriptSelectComponent,
	]
})
export class CharacterModule { }
