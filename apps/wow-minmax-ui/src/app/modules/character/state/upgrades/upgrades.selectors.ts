import { createSelector } from "@ngrx/store";
import { ItemSlotGroup } from "../../model/upgrade/ItemSlotGroup";
import { Upgrade } from "../../model/upgrade/Upgrade";
import { CharacterModuleState } from "../character-module.state";

const selectUpgradesState = (state: CharacterModuleState) => state.upgrades;

export const selectItemFilter = createSelector(
	selectUpgradesState,
	state => state.itemFilter
);

export const selectAllUpgrades = createSelector(
	selectUpgradesState,
	state => {
		if (Object.values(state.upgrades).every(x => x.value.length === 0)) {
			return null;
		}
		
		const result = { } as Record<ItemSlotGroup, Upgrade[]>;
		Object.values(ItemSlotGroup).forEach(
			k => result[k] = state.upgrades[k].value
		);
		return result;
	}
);

export function selectUpgrades(slotGroup: ItemSlotGroup) {
	return createSelector(
		selectUpgradesState,
		state => state.upgrades[slotGroup].value
	);
}
