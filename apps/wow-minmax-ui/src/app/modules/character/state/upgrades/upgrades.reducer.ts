import { createReducer, on } from "@ngrx/store";
import { failure, Loadable, pending, success } from '../../../shared/state/Loadable';
import { ItemFilter } from '../../model/equipment/ItemFilter';
import { ItemSlotGroup } from "../../model/upgrade/ItemSlotGroup";
import { Upgrade } from "../../model/upgrade/Upgrade";
import { loadAllUpgradesFailure, loadAllUpgradesSuccess, updateItemFilter } from "./upgrades.actions";

export interface UpgradesState {
	itemFilter: ItemFilter;
	upgrades: Record<ItemSlotGroup, Loadable<Upgrade[]>>
}

const initialState: UpgradesState = {
	itemFilter: {
		heroics: true,
		raids: true,
		worldBosses: false,
		pvpItems: false,
		greens: true,
		legendaries: false
	},
	upgrades: withAllSlotsGroupsSetTo(pending([]))
};

export const upgradesReducer = createReducer(
	initialState,

	on(updateItemFilter, (state, { itemFilter }) => ({
		...state,
		itemFilter: patchFilterValues(state.itemFilter, itemFilter)
	})),

	on(loadAllUpgradesSuccess, (state, { slotGroup, upgrades }) => ({
		...state,
		upgrades: withSlotsGroupSetTo(state.upgrades, slotGroup, success(upgrades))
	})),
	on(loadAllUpgradesFailure, (state, { slotGroup, error }) => ({
		...state,
		upgrades: withSlotsGroupSetTo(state.upgrades, slotGroup, failure([], error))
	}))
);

function withAllSlotsGroupsSetTo(value: Loadable<Upgrade[]>) {
	return {
		HEAD: value,
		NECK: value,
		SHOULDER: value,
		BACK: value,
		CHEST: value,
		WRIST: value,
		HANDS: value,
		WAIST: value,
		LEGS: value,
		FEET: value,
		FINGERS: value,
		TRINKETS: value,
		WEAPONS: value,
		RANGED: value
	}
}

function withSlotsGroupSetTo(upgrades: Record<ItemSlotGroup, Loadable<Upgrade[]>>, key: ItemSlotGroup, value: Loadable<Upgrade[]>) {
	const result = { ...upgrades };
	result[key] = value;
	return result;
}

function patchFilterValues(itemFilter: ItemFilter, valuesToPatch: Partial<ItemFilter>) {
	const newItemFilter = { ...itemFilter };

	Object.keys(valuesToPatch).forEach(k => {
		const key = k as keyof ItemFilter;
		const value = valuesToPatch[key];
		if (value !== undefined) {
			newItemFilter[key] = value;
		}
	});

	return newItemFilter;
}

