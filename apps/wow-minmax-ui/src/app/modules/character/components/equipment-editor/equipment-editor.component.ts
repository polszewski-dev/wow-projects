import { Component } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { DropdownSelectValueFormatter } from 'src/app/modules/shared/components/dropdown-select/dropdown-select.component';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { ItemType } from '../../model/equipment/ItemType';
import { CharacterModuleState } from '../../state/character-module.state';
import { equipGearSet, resetEquipment } from '../../state/character/character.actions';
import { selectCharacterId, selectEquipment } from '../../state/character/character.selectors';
import { selectEquipmentOptions } from '../../state/equipment-options/equipment-options.selectors';
import { filter, map, switchMap } from 'rxjs';
import { EquipmentService } from '../../services/equipment.service';

@Component({
	selector: 'app-equipment-editor',
	templateUrl: './equipment-editor.component.html',
	styleUrls: ['./equipment-editor.component.css']
})
export class EquipmentEditorComponent {
	readonly data$ = this.store.select(dataSelector);
	readonly gearSets$ = this.store.select(selectCharacterId).pipe(
		filter(characterId => !!characterId),
		switchMap(characterId => this.equipmentService.getAvailableGearSets(characterId!))
	);

	constructor(private store: Store<CharacterModuleState>, private equipmentService: EquipmentService) {}

	resetEquipment(characterId: string) {
		this.store.dispatch(resetEquipment({ characterId }));
	}

	equipGearSet(characterId: string, gearSet: string) {
		this.store.dispatch(equipGearSet({ characterId, gearSet }));
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
