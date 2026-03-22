import { createReducer, on } from "@ngrx/store";
import { failure, Loadable, loading, pending, success } from '../../../shared/state/Loadable';
import { AssetGroup } from "../../model/Asset";
import { BuffGroup } from "../../model/Buff";
import { Character } from '../../model/Character';
import { ConsumableGroup } from "../../model/Consumable";
import { Equipment } from '../../model/equipment/Equipment';
import { EquipmentSocketStatus } from '../../model/equipment/EquipmentSocketStatus';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { EquipmentDiff } from "../../model/equipment/ItemSlotStatus";
import { copyOptionGroup, OptionGroup } from "../../model/OptionGroup";
import { OptionStatus } from "../../model/OptionStatus";
import { changeAssetStatusSuccess, changeBuffStatusSuccess, changeConsumableStatusSuccess, changeProfessionSuccess, changeScriptSuccess, changeTalentLinkSuccess, dpsChanged, equipEnchantSuccess, equipGearSetSuccess, equipGemSuccess, equipItemBestVariantSuccess, equipItemGroupSuccess, equipPreviousPhaseSuccess, loadAssetStatuses, loadAssetStatusesFailure, loadAssetStatusesSuccess, loadBuffStatuses, loadBuffStatusesFailure, loadBuffStatusesSuccess, loadCharacter, loadCharacterFailure, loadCharacterSuccess, loadConsumableStatuses, loadConsumableStatusesSuccess, loadEquipment, loadEquipmentFailure, loadEquipmentSuccess, loadSocketStatusFailure, loadSocketStatusSuccess, resetEquipmentSuccess, selectCharacter } from './character.actions';

export interface CharacterState {
	playerId: string | null;
	character: Loadable<Character | null>;
	equipment: Record<ItemSlot, Loadable<EquippableItem | null>>;
	socketStatus: Loadable<EquipmentSocketStatus | null>;
	buffStatuses: Loadable<BuffGroup[]>;
	consumableStatuses: Loadable<ConsumableGroup[]>;
	assetStatuses: Loadable<AssetGroup[]>;
	dpsChangeIdx: number;
}

const initialState: CharacterState = {
	playerId: null,
	character: pending(null),
	equipment: withAllSlotsSetTo(pending(null)),
	socketStatus: pending(null),
	buffStatuses: pending([]),
	consumableStatuses: pending([]),
	assetStatuses: pending([]),
	dpsChangeIdx: 0
};

export const characterReducer = createReducer(
	initialState,
	on(selectCharacter, (state, { playerId }) => ({
		...state,
		playerId
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

	on(loadBuffStatuses, (state) => ({
		...state,
		buffStatuses: loading([])
	})),
	on(loadBuffStatusesSuccess, (state, { buffStatuses }) => ({
		...state,
		buffStatuses: success(buffStatuses)
	})),
	on(loadBuffStatusesFailure, (state, { error }) => ({
		...state,
		buffStatuses: failure([], error)
	})),

	on(loadConsumableStatuses, (state) => ({
		...state,
		consumableStatuses: loading([])
	})),
	on(loadConsumableStatusesSuccess, (state, { consumableStatuses }) => ({
		...state,
		consumableStatuses: success(consumableStatuses)
	})),

	on(loadAssetStatuses, (state) => ({
		...state,
		assetStatuses: loading([])
	})),
	on(loadAssetStatusesSuccess, (state, { assetStatuses }) => ({
		...state,
		assetStatuses: success(assetStatuses)
	})),
	on(loadAssetStatusesFailure, (state, { error }) => ({
		...state,
		assetStatuses: failure([], error)
	})),

	on(equipItemBestVariantSuccess, (state, { equipmentDiff }) => ({
		...state,
		equipment: withEquipmentDiffApplied(state.equipment, equipmentDiff)
	})),
	on(equipItemGroupSuccess, (state, { equipmentDiff }) => ({
		...state,
		equipment: withEquipmentDiffApplied(state.equipment, equipmentDiff)
	})),
	on(equipEnchantSuccess, (state, { equipmentDiff }) => ({
		...state,
		equipment: withEquipmentDiffApplied(state.equipment, equipmentDiff)
	})),
	on(equipGemSuccess, (state, { equipmentDiff }) => ({
		...state,
		equipment: withEquipmentDiffApplied(state.equipment, equipmentDiff)
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

	on(changeBuffStatusSuccess, (state, { buffStatus }) => ({
		...state,
		buffStatuses: withChangedStatus(state.buffStatuses, buffStatus)
	})),

	on(changeConsumableStatusSuccess, (state, { consumableStatus }) => ({
		...state,
		consumableStatuses: withChangedStatus(state.consumableStatuses, consumableStatus)
	})),

	on(changeAssetStatusSuccess, (state, { assetStatus }) => ({
		...state,
		assetStatuses: withChangedStatus(state.assetStatuses, assetStatus)
	})),

	on(dpsChanged, (state) => ({
		...state,
		dpsChangeIdx: state.dpsChangeIdx + 1
	})),

	on(changeProfessionSuccess, (state, { character }) => ({
		...state,
		character: success(character)
	})),

	on(changeTalentLinkSuccess, (state, { character }) => ({
		...state,
		character: success(character)
	})),

	on(changeScriptSuccess, (state, { character }) => ({
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

function withEquipmentDiffApplied(
	equipment: Record<ItemSlot, Loadable<EquippableItem | null>>,
	equipmentDiff: EquipmentDiff
) {
	const result = { ...equipment };

	for (const status of equipmentDiff) {
		result[status.itemSlot] = success(status.item);
	}

	return result;
}

function withChangedStatus<T extends { id: number }>(groups: Loadable<OptionGroup<T>[]>, status: OptionStatus<T>): Loadable<OptionGroup<T>[]> {
	const withChangedStatus = groups.value.map(group => changeOptionStatus(group, status));

	return {
		...groups,
		value: withChangedStatus
	};
}

function changeOptionStatus<T extends { id: number }>(group: OptionGroup<T>, status: OptionStatus<T>) {
	const index = group.statuses.findIndex(x => x.option.id == status.option.id);

	if (index < 0) {
		return group;
	}

	const copy = copyOptionGroup(group)

	copy.statuses.forEach(status => status.enabled = false);
	copy.statuses[index] = status;
	return copy;
}
