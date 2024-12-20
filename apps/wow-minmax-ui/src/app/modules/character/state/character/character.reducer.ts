import { createReducer, on } from "@ngrx/store";
import { failure, Loadable, loading, pending, success } from '../../../shared/state/Loadable';
import { Buff } from "../../model/buff/Buff";
import { BuffListType } from '../../model/buff/BuffListType';
import { Character } from "../../model/Character";
import { Consumable } from "../../model/consumable/Consumable";
import { Equipment } from '../../model/equipment/Equipment';
import { EquipmentSocketStatus } from '../../model/equipment/EquipmentSocketStatus';
import { EquippableItem } from "../../model/equipment/EquippableItem";
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { getSlots, ItemSlotGroup } from "../../model/upgrade/ItemSlotGroup";
import { dpsChanged, enableBuffSuccess, enableConsumableSuccess, equipEnchantSuccess, equipGemSuccess, equipItemBestVariantSuccess, equipItemGroupSuccess, loadBuffListFailure, loadBuffListSuccess, loadBuffs, loadCharacter, loadCharacterFailure, loadCharacterSuccess, loadConsumables, loadConsumablesFailure, loadConsumablesSuccess, loadEquipment, loadEquipmentFailure, loadEquipmentSuccess, loadSocketStatusFailure, loadSocketStatusSuccess, resetEquipmentSuccess, selectCharacter } from "./character.actions";

export interface CharacterState {
	characterId: string | null;
	character: Loadable<Character | null>;
	equipment: Record<ItemSlot, Loadable<EquippableItem | null>>;
	socketStatus: Loadable<EquipmentSocketStatus | null>;
	buffs: Record<BuffListType, Loadable<Buff[]>>;
	consumables: Loadable<Consumable[]>;
	dpsChangeIdx: number;
}

const initialState: CharacterState = {
	characterId: null,
	character: pending(null),
	equipment: withAllSlotsSetTo(pending(null)),
	socketStatus: pending(null),
	buffs: withAllBuffListsSetTo(pending([])),
	consumables: pending([]),
	dpsChangeIdx: 0
};

export const characterReducer = createReducer(
	initialState,
	on(selectCharacter, (state, { characterId }) => ({
		...state,
		characterId
	})),

	on(loadCharacter, (state) => ({
		...state,
		character: loading(null)
	})),
	on(loadCharacterSuccess, (state, { character }) => ({
		...state,
		character: success(character)
	})),
	on(loadCharacterFailure, (state, { error }) => ({
		...state,
		character: failure(null, error)
	})),

	on(loadEquipment, (state) => ({
		...state,
		equipment: withAllSlotsSetTo(loading(null))
	})),
	on(loadEquipmentSuccess, (state, { equipment }) => ({
		...state,
		equipment: withAllSlotsFilledFrom(equipment),
	})),
	on(loadEquipmentFailure, (state, { error }) => ({
		...state,
		equipment: withAllSlotsSetTo(failure(null, error))
	})),

	on(loadSocketStatusSuccess, (state, { socketStatus }) => ({
		...state,
		socketStatus: success(socketStatus)
	})),
	on(loadSocketStatusFailure, (state, { error }) => ({
		...state,
		socketStatus: failure(null, error)
	})),

	on(loadBuffs, (state) => ({
		...state,
		buffs: withAllBuffListsSetTo(loading([]))
	})),
	on(loadBuffListSuccess, (state, { buffListType, buffList }) => ({
		...state,
		buffs: withBuffListSetTo(state.buffs, buffListType, success(buffList))
	})),
	on(loadBuffListFailure, (state, { buffListType, error }) => ({
		...state,
		buffs: withBuffListSetTo(state.buffs, buffListType, failure([], error))
	})),

	on(loadConsumables, (state) => ({
		...state,
		consumables: loading([])
	})),
	on(loadConsumablesSuccess, (state, { consumables }) => ({
		...state,
		consumables: success(consumables)
	})),

	on(equipItemBestVariantSuccess, (state, { itemSlot, equippableItem }) => ({
		...state,
		equipment: withSlotSetTo(state.equipment, itemSlot, success(equippableItem))
	})),
	on(equipItemGroupSuccess, (state, { slotGroup, items }) => ({
		...state,
		equipment: withSlotGroupSetTo(state.equipment, slotGroup, idx => success(items[idx] || null))
	})),
	on(equipEnchantSuccess, (state, { itemSlot, equippableItem }) => ({
		...state,
		equipment: withSlotSetTo(state.equipment, itemSlot, success(equippableItem))
	})),
	on(equipGemSuccess, (state, { itemSlot, equippableItem }) => ({
		...state,
		equipment: withSlotSetTo(state.equipment, itemSlot, success(equippableItem))
	})),
	on(resetEquipmentSuccess, (state) => ({
		...state,
		equipment: withAllSlotsSetTo(success(null))
	})),

	on(enableBuffSuccess, (state, { buffListType, buffList }) => ({
		...state,
		buffs: withBuffListSetTo(state.buffs, buffListType, success(buffList))
	})),

	on(enableConsumableSuccess, (state, { consumables }) => ({
		...state,
		consumables: success(consumables)
	})),

	on(dpsChanged, (state) => ({
		...state,
		dpsChangeIdx: state.dpsChangeIdx + 1
	}))
);

function withAllSlotsSetTo(value: Loadable<EquippableItem | null>) {
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

function withAllSlotsFilledFrom(equipment: Equipment) {
	const get = (slot: ItemSlot) => success(equipment.itemsBySlot[slot] || null);

	return {
		HEAD: get(ItemSlot.HEAD),
		NECK: get(ItemSlot.NECK),
		SHOULDER: get(ItemSlot.SHOULDER),
		BACK: get(ItemSlot.BACK),
		CHEST: get(ItemSlot.CHEST),
		WRIST: get(ItemSlot.WRIST),
		HANDS: get(ItemSlot.HANDS),
		WAIST: get(ItemSlot.WAIST),
		LEGS: get(ItemSlot.LEGS),
		FEET: get(ItemSlot.FEET),
		FINGER_1: get(ItemSlot.FINGER_1),
		FINGER_2: get(ItemSlot.FINGER_2),
		TRINKET_1: get(ItemSlot.TRINKET_1),
		TRINKET_2: get(ItemSlot.TRINKET_2),
		MAIN_HAND: get(ItemSlot.MAIN_HAND),
		OFF_HAND: get(ItemSlot.OFF_HAND),
		RANGED: get(ItemSlot.RANGED),
	}
}

function withSlotSetTo(
	equipment: Record<ItemSlot, Loadable<EquippableItem | null>>,
	key: ItemSlot,
	value: Loadable<EquippableItem | null>
) {
	const result = { ...equipment };
	result[key] = value;
	return result;
}

function withSlotGroupSetTo(
	equipment: Record<ItemSlot, Loadable<EquippableItem | null>>,
	slotGroup: ItemSlotGroup,
	itemAccessor: (idx: number) => Loadable<EquippableItem | null>
) {
	const result = { ...equipment };

	getSlots(slotGroup).forEach((key, idx) => {
		result[key] = itemAccessor(idx);
	});

	return result;
}

function withAllBuffListsSetTo(value: Loadable<Buff[]>) {
	return {
		CHARACTER_BUFF: value,
		TARGET_DEBUFF: value
	}
}

function withBuffListSetTo(buffs: Record<BuffListType, Loadable<Buff[]>>, key: BuffListType, value: Loadable<Buff[]>) {
	const result = { ...buffs };
	result[key] = value;
	return result;
}
