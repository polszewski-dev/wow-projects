import { Component } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { filter, switchMap } from 'rxjs';
import { PhaseId } from 'src/app/modules/shared/model/character/PhaseId';
import { parsePlayerId } from '../../model/PlayerId';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { ItemType } from '../../model/equipment/ItemType';
import { EquipmentService } from '../../services/equipment.service';
import { CharacterModuleState } from '../../state/character-module.state';
import { equipGearSet, equipPreviousPhase, resetEquipment } from '../../state/character/character.actions';
import { selectPlayerId, selectEquipment } from '../../state/character/character.selectors';
import { selectEquipmentOptions } from '../../state/equipment-options/equipment-options.selectors';

@Component({
	selector: 'app-equipment-editor',
	templateUrl: './equipment-editor.component.html',
	styleUrls: ['./equipment-editor.component.css']
})
export class EquipmentEditorComponent {
	readonly data$ = this.store.select(dataSelector);
	readonly gearSets$ = this.store.select(selectPlayerId).pipe(
		filter(playerId => !!playerId),
		switchMap(playerId => this.equipmentService.getAvailableGearSets(playerId!))
	);

	constructor(private store: Store<CharacterModuleState>, private equipmentService: EquipmentService) {}

	resetEquipment(playerId: string) {
		this.store.dispatch(resetEquipment({ playerId }));
	}

	equipGearSet(playerId: string, gearSet: string) {
		this.store.dispatch(equipGearSet({ playerId, gearSet }));
	}

	equipPreviousPhase(playerId: string) {
		this.store.dispatch(equipPreviousPhase({ playerId }));
	}

	hasPreviousPhase(playerId: string) {
		const phaseId = parsePlayerId(playerId).phaseId;
		return phaseId.toLocaleLowerCase() !== PhaseId.VANILLA_P1.toLocaleLowerCase();
	}

	readonly itemSlots = Object.values(ItemSlot);
	readonly ItemSlot = ItemSlot;
	readonly ItemType = ItemType;
}

const dataSelector = createSelector(
	selectPlayerId,
	selectEquipment,
	selectEquipmentOptions,
	(playerId, equipment, equipmentOptions) => {
		if (!playerId || !equipment || !equipmentOptions) {
			return null;
		}

		return {
			playerId,
			equipment,
			equipmentOptions,
			canEditGems: equipmentOptions.editGems,
			isSlotVisible: (itemSlot: ItemSlot) => (itemSlot !== ItemSlot.OFF_HAND) || (equipment[ItemSlot.MAIN_HAND]?.item?.itemType !== ItemType.TWO_HAND)
		};
	}
);
