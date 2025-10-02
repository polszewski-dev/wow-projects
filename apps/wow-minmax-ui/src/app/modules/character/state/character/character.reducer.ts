import { createReducer, on } from "@ngrx/store";
import { failure, Loadable, loading, pending, success } from '../../../shared/state/Loadable';
import { BuffListType } from '../../model/buff/BuffListType';
import { BuffStatus } from "../../model/buff/BuffStatus";
import { Character } from '../../model/Character';
import { ConsumableStatus } from "../../model/consumable/ConsumableStatus";
import { Equipment } from '../../model/equipment/Equipment';
import { EquipmentSocketStatus } from '../../model/equipment/EquipmentSocketStatus';
import { EquippableItem } from "../../model/equipment/EquippableItem";
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { getSlots, ItemSlotGroup } from "../../model/upgrade/ItemSlotGroup";
import { changeBuffStatusSuccess, changeConsumableStatusSuccess, changeProfessionSuccess, dpsChanged, equipEnchantSuccess, equipGearSetSuccess, equipGemSuccess, equipItemBestVariantSuccess, equipItemGroupSuccess, equipPreviousPhaseSuccess, loadBuffListFailure, loadBuffListSuccess, loadBuffs, loadCharacter, loadCharacterFailure, loadCharacterSuccess, loadConsumableStatuses, loadConsumableStatusesSuccess, loadEquipment, loadEquipmentFailure, loadEquipmentSuccess, loadSocketStatusFailure, loadSocketStatusSuccess, resetEquipmentSuccess, selectCharacter } from './character.actions';

export interface CharacterState {
	characterId: string | null;
	character: Loadable<Character | null>;
	equipment: Record<ItemSlot, Loadable<EquippableItem | null>>;
	socketStatus: Loadable<EquipmentSocketStatus | null>;
	buffStatuses: Record<BuffListType, Loadable<BuffStatus[]>>;
	consumableStatuses: Loadable<ConsumableStatus[]>;
	dpsChangeIdx: number;
}

const initialState: CharacterState = {
	characterId: null,
	character: pending(null),
	equipment: withAllSlotsSetTo(pending(null)),
	socketStatus: pending(null),
	buffStatuses: withAllBuffListsSetTo(pending([])),
	consumableStatuses: pending([]),
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
		buffStatuses: withAllBuffListsSetTo(loading([]))
	})),
	on(loadBuffListSuccess, (state, { buffListType, buffStatusList }) => ({
		...state,
		buffStatuses: withBuffListSetTo(state.buffStatuses, buffListType, success(buffStatusList))
	})),
	on(loadBuffListFailure, (state, { buffListType, error }) => ({
		...state,
		buffStatuses: withBuffListSetTo(state.buffStatuses, buffListType, failure([], error))
	})),

	on(loadConsumableStatuses, (state) => ({
		...state,
		consumableStatuses: loading([])
	})),
	on(loadConsumableStatusesSuccess, (state, { consumableStatuses }) => ({
		...state,
		consumableStatuses: success(consumableStatuses)
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
	on(equipGearSetSuccess, (state, { equipment }) => ({
		...state,
		equipment: withAllSlotsFilledFrom(equipment),
	})),
	on(equipPreviousPhaseSuccess, (state, { equipment }) => ({
		...state,
		equipment: withAllSlotsFilledFrom(equipment),
	})),

	on(changeBuffStatusSuccess, (state, { buffListType, buffStatusList }) => ({
		...state,
		buffStatuses: withBuffListSetTo(state.buffStatuses, buffListType, success(buffStatusList))
	})),

	on(changeConsumableStatusSuccess, (state, { consumableStatuses }) => ({
		...state,
		consumableStatuses: success(consumableStatuses)
	})),

	on(dpsChanged, (state) => ({
		...state,
		dpsChangeIdx: state.dpsChangeIdx + 1
	})),

	on(changeProfessionSuccess, (state, { character }) => ({
		...state,
		character: success(character)
	})),
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

function withAllBuffListsSetTo(value: Loadable<BuffStatus[]>) {
	return {
		CHARACTER_BUFF: value,
		TARGET_DEBUFF: value
	}
}

function withBuffListSetTo(buffStatuses: Record<BuffListType, Loadable<BuffStatus[]>>, key: BuffListType, value: Loadable<BuffStatus[]>) {
	const result = { ...buffStatuses };
	result[key] = value;
	return result;
}
