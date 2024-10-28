import { createSelector } from "@ngrx/store";
import { ItemSlot } from "../../model/equipment/ItemSlot";
import { CharacterModuleState } from "../character-module.state";

const selectEquipmentOptionsState = (state: CharacterModuleState) => state.equipmentOptions;

export const selectEquipmentOptions = createSelector(
	selectEquipmentOptionsState,
	state => state.equipmentOptions.value
);

export function selectItemOptions(itemSlot: ItemSlot) {
	return createSelector(
		selectEquipmentOptionsState,
		state => {
			const replacedSlot = itemSlot == ItemSlot.FINGER_2 ? ItemSlot.FINGER_1 : itemSlot == ItemSlot.TRINKET_2 ? ItemSlot.TRINKET_1 : itemSlot;
			return state.itemOptions[replacedSlot].value;
		}
	);
}

export const selectEnchantOptions = createSelector(
	selectEquipmentOptionsState,
	state => state.enchantOptions.value
);

export const selectGemOptions = createSelector(
	selectEquipmentOptionsState,
	state => state.gemOptions.value
);