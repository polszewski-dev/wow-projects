import { Component } from '@angular/core';
import { combineLatest, map } from 'rxjs';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { ItemType } from '../../model/equipment/ItemType';
import { CharacterStateService } from '../../services/character-state.service';
import { EquipmentOptionsStateService } from '../../services/equipment-options-state.service';

@Component({
	selector: 'app-equipment-editor',
	templateUrl: './equipment-editor.component.html',
	styleUrls: ['./equipment-editor.component.css']
})
export class EquipmentEditorComponent {
	readonly dataLoaded$ = combineLatest([
		this.characterStateService.equipment$,
		this.equipmentOptionsStateService.equipmentOptions$
	]).pipe(
		map(([equipment, equipmentOptions]) => !!equipment && !!equipmentOptions)
	);

	readonly itemSlots = Object.values(ItemSlot);
	readonly ItemSlot = ItemSlot;
	readonly ItemType = ItemType;

	constructor(
		private characterStateService: CharacterStateService,
		private equipmentOptionsStateService: EquipmentOptionsStateService
	) {}

	isSlotVisible(itemSlot: ItemSlot) {
		return itemSlot !== ItemSlot.OFF_HAND || this.characterStateService.equipmentSnapshot!.itemsBySlot[ItemSlot.MAIN_HAND]?.item?.itemType !== ItemType.TWO_HAND
	}

	get editGems() {
		 return this.equipmentOptionsStateService.equipmentOptionsSnapshot!.editGems;
	}

	resetEquipment() {
		this.characterStateService.resetEquipment();
	}
}
