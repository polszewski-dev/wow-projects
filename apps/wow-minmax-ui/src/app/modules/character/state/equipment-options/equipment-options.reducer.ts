import { createReducer, on } from "@ngrx/store";
import { failure, Loadable, loading, pending, success } from '../../../shared/state/Loadable';
import { EnchantOptions } from "../../model/equipment/EnchantOptions";
import { EquipmentOptions } from '../../model/equipment/EquipmentOptions';
import { GemOptions } from '../../model/equipment/GemOptions';
import { ItemOptions } from "../../model/equipment/ItemOptions";
import { ItemSlot } from "../../model/equipment/ItemSlot";
import { loadEnchantOptions, loadEnchantOptionsFailure, loadEnchantOptionsSuccess, loadEquipmentOptions, loadEquipmentOptionsFailure, loadEquipmentOptionsSuccess, loadGemOptions, loadGemOptionsFailure, loadGemOptionsSuccess, loadItemOptions, loadItemOptionsFailure, loadItemOptionsSuccess } from "./equipment-options.actions";

export interface EquipmentOptionsState {
	equipmentOptions: Loadable<EquipmentOptions | null>;
	itemOptions: Record<ItemSlot, Loadable<ItemOptions | null>>;
	enchantOptions: Loadable<EnchantOptions[]>;
	gemOptions: Loadable<GemOptions[]>;
}

const initialState: EquipmentOptionsState = {
	equipmentOptions: pending(null),
	itemOptions: withAllSlotsSetTo(pending(null)),
	enchantOptions: pending([]),
	gemOptions: pending([])
};

export const equipmentOptionsReducer = createReducer(
	initialState,

	on(loadEquipmentOptions, (state) => ({
		...state,
		equipmentOptions: loading(null),
	})),
	on(loadEquipmentOptionsSuccess, (state, { equipmentOptions }) => ({
		...state,
		equipmentOptions: success(equipmentOptions),
	})),
	on(loadEquipmentOptionsFailure, (state, { error }) => ({
		...state,
		equipmentOptions: failure(null, error),
	})),

	on(loadItemOptions, (state) => ({
		...state,
		itemOptions: withAllSlotsSetTo(loading(null)),
	})),
	on(loadItemOptionsSuccess, (state, { itemSlot, itemOptions }) => ({
		...state,
		itemOptions: withSlotSetTo(state.itemOptions, itemSlot, success(itemOptions)),
	})),
	on(loadItemOptionsFailure, (state, { itemSlot, error }) => ({
		...state,
		itemOptions: withSlotSetTo(state.itemOptions, itemSlot, failure(null, error)),
	})),

	on(loadEnchantOptions, (state) => ({
		...state,
		enchantOptions: loading([]),
	})),
	on(loadEnchantOptionsSuccess, (state, { enchantOptions }) => ({
		...state,
		enchantOptions: success(enchantOptions),
	})),
	on(loadEnchantOptionsFailure, (state, { error }) => ({
		...state,
		enchantOptions: failure([], error),
	})),

	on(loadGemOptions, (state) => ({
		...state,
		gemOptions: loading([]),
	})),
	on(loadGemOptionsSuccess, (state, { gemOptions }) => ({
		...state,
		gemOptions: success(gemOptions),
	})),
	on(loadGemOptionsFailure, (state, { error }) => ({
		...state,
		gemOptions: failure([], error),
	})),
);

function withAllSlotsSetTo(value: Loadable<ItemOptions | null>) {
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
		FINGER_1: value,
		FINGER_2: value,
		TRINKET_1: value,
		TRINKET_2: value,
		MAIN_HAND: value,
		OFF_HAND: value,
		RANGED: value
	}
}

function withSlotSetTo(buffs: Record<ItemSlot, Loadable<ItemOptions | null>>, key: ItemSlot, value: Loadable<ItemOptions | null>) {
	const result = { ...buffs };
	result[key] = value;
	return result;
}
