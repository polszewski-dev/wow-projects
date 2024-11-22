import { createSelector } from "@ngrx/store";
import { Loadable } from "../../../shared/state/Loadable";
import { LoadStatus } from "../../../shared/state/LoadStatus";
import { BuffListType } from '../../model/buff/BuffListType';
import { EquippableItem } from "../../model/equipment/EquippableItem";
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { CharacterModuleState } from "../character-module.state";

const selectCharacterState = (state: CharacterModuleState) => state.character;

export const selectCharacterId = createSelector(
	selectCharacterState,
	state => state.characterId
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

export function selectBuffList(buffListType: BuffListType) {
	return createSelector(
		selectCharacterState,
		state => state.buffs[buffListType].value
	);
}

export const selectConsumables = createSelector(
	selectCharacterState,
	state => state.consumables.value
);


export const selectDpsChangeIdx = createSelector(
	selectCharacterState,
	state => state.dpsChangeIdx
);

export const selectDpsChanges = createSelector(
	selectCharacterId,
	selectDpsChangeIdx,
	(characterId, dpsChangeIdx) => {
		return { characterId, dpsChangeIdx };
	}
);

function allEquipmentSlotsLoaded(equipment: Record<ItemSlot, Loadable<EquippableItem | null>>) {
	return Object.values(equipment).every(x => x.status === LoadStatus.SUCCESS || x.status === LoadStatus.ERROR);
}
