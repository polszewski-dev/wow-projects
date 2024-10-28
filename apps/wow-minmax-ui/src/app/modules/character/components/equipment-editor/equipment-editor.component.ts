import { Component } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { ItemType } from '../../model/equipment/ItemType';
import { CharacterModuleState } from '../../state/character-module.state';
import { resetEquipment } from '../../state/character/character.actions';
import { selectCharacterId, selectEquipment } from '../../state/character/character.selectors';
import { selectEquipmentOptions } from '../../state/equipment-options/equipment-options.selectors';

@Component({
	selector: 'app-equipment-editor',
	templateUrl: './equipment-editor.component.html',
	styleUrls: ['./equipment-editor.component.css']
})
export class EquipmentEditorComponent {
	readonly data$ = this.store.select(dataSelector);

	constructor(private store: Store<CharacterModuleState>) {}

	resetEquipment(characterId: string) {
		this.store.dispatch(resetEquipment({ characterId }));
	}

	readonly itemSlots = Object.values(ItemSlot);
	readonly ItemSlot = ItemSlot;
	readonly ItemType = ItemType;
}

const dataSelector = createSelector(
	selectCharacterId,
	selectEquipment,
	selectEquipmentOptions,
	(characterId, equipment, equipmentOptions) => {
		if (!characterId || !equipment || !equipmentOptions) {
			return null;
		}

		return {
			characterId,
			equipment,
			equipmentOptions,
			canEditGems: equipmentOptions.editGems,
			isSlotVisible: (itemSlot: ItemSlot) => (itemSlot !== ItemSlot.OFF_HAND) || (equipment[ItemSlot.MAIN_HAND]?.item?.itemType !== ItemType.TWO_HAND)
		};
	}
);
