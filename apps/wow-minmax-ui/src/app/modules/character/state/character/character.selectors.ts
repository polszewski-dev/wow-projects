import { createSelector } from "@ngrx/store";
import { Loadable } from "../../../shared/state/Loadable";
import { LoadStatus } from "../../../shared/state/LoadStatus";
import { EquippableItem } from "../../model/equipment/EquippableItem";
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { CharacterModuleState } from "../character-module.state";

const selectCharacterState = (state: CharacterModuleState) => state.character;

export const selectPlayerId = createSelector(
	selectCharacterState,
	state => state.playerId
)

export const selectCharacter = createSelector(
	selectCharacterState,
	state => state.character.value
)

export const selectEquipment = createSelector(
	selectCharacterState,
	state => {
		if (!allEquipmentSlotsLoaded(state.equipment)) {
			return null;
		}

		const result = {} as Record<ItemSlot, EquippableItem | null>;
		Object.values(ItemSlot).forEach(
			k => result[k] = state.equipment[k].value
		);
		return result;
	}
)

export function selectEquipmentSlot(itemSlot: ItemSlot) {
	return createSelector(
		selectCharacterState,
		state => state.equipment[itemSlot].value
	);
}

export function selectSocketStatus(itemSlot: ItemSlot) {
	return createSelector(
		selectCharacterState,
		state => state.socketStatus.value?.socketStatusesByItemSlot[itemSlot] || null
	);
}

export const selectBuffStatuses = createSelector(
	selectCharacterState,
	state => state.buffStatuses.value
);

export const selectConsumableStatuses = createSelector(
	selectCharacterState,
	state => state.consumableStatuses.value
);

export const selectAssetStatuses = createSelector(
	selectCharacterState,
	state => state.assetStatuses.value
);

export const selectDpsChangeIdx = createSelector(
	selectCharacterState,
	state => state.dpsChangeIdx
);

export const selectDpsChanges = createSelector(
	selectPlayerId,
	selectDpsChangeIdx,
	(playerId, dpsChangeIdx) => {
		return { playerId, dpsChangeIdx };
	}
);

function allEquipmentSlotsLoaded(equipment: Record<ItemSlot, Loadable<EquippableItem | null>>) {
	return Object.values(equipment).every(x => x.status === LoadStatus.SUCCESS || x.status === LoadStatus.ERROR);
}
