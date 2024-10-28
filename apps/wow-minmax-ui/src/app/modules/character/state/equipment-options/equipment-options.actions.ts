import { createAction, props } from "@ngrx/store";
import { EnchantOptions } from "../../model/equipment/EnchantOptions";
import { EquipmentOptions } from '../../model/equipment/EquipmentOptions';
import { GemOptions } from "../../model/equipment/GemOptions";
import { ItemOptions } from '../../model/equipment/ItemOptions';
import { ItemSlot } from '../../model/equipment/ItemSlot';

export const loadEquipmentOptions = createAction(
	"[Equipment Options] Load Equipment Options",
	props<{ characterId: string }>()
);

export const loadEquipmentOptionsSuccess = createAction(
	"[Equipment Options] Load Equipment Options Success",
	props<{ equipmentOptions: EquipmentOptions }>()
);

export const loadEquipmentOptionsFailure = createAction(
	"[Equipment Options] Load Equipment Options Failure",
	props<{ error: string }>()
);

export const loadItemOptions = createAction(
	"[Equipment Options] Load Item Options",
	props<{ characterId: string }>()
);

export const loadItemOptionsSuccess = createAction(
	"[Equipment Options] Load Item Options Success",
	props<{ itemSlot: ItemSlot, itemOptions: ItemOptions }>()
);

export const loadItemOptionsFailure = createAction(
	"[Equipment Options] Load Item Options Failure",
	props<{ itemSlot: ItemSlot, error: string }>()
);

export const loadEnchantOptions = createAction(
	"[Equipment Options] Load Enchant Options",
	props<{ characterId: string }>()
);

export const loadEnchantOptionsSuccess = createAction(
	"[Equipment Options] Load Enchant Options Success",
	props<{ enchantOptions: EnchantOptions[] }>()
);

export const loadEnchantOptionsFailure = createAction(
	"[Equipment Options] Load Enchant Options Failure",
	props<{ error: string }>()
);

export const loadGemOptions = createAction(
	"[Equipment Options] Load Gem Options",
	props<{ characterId: string }>()
);

export const loadGemOptionsSuccess = createAction(
	"[Equipment Options] Load Gem Options Success",
	props<{ gemOptions: GemOptions[] }>()
);

export const loadGemOptionsFailure = createAction(
	"[Equipment Options] Load Gem Options Failure",
	props<{ error: string }>()
);
