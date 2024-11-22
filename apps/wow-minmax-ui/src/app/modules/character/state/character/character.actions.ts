import { createAction, props } from "@ngrx/store";
import { Buff } from '../../model/buff/Buff';
import { BuffListType } from '../../model/buff/BuffListType';
import { Character } from '../../model/Character';
import { Enchant } from "../../model/equipment/Enchant";
import { Equipment } from '../../model/equipment/Equipment';
import { EquipmentSocketStatus } from '../../model/equipment/EquipmentSocketStatus';
import { Gem } from "../../model/equipment/Gem";
import { Item } from "../../model/equipment/Item";
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { ItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { Consumable } from "../../model/consumable/Consumable";

export const selectCharacter = createAction(
	"[Character] Select Character",
	props<{ characterId: string | null }>()
);

export const loadCharacter = createAction(
	"[Character] Load Character",
	props<{ characterId: string }>()
);

export const loadCharacterSuccess = createAction(
	"[Character] Load Character Success",
	props<{ character: Character }>()
);

export const loadCharacterFailure = createAction(
	"[Character] Load Character Failure",
	props<{ error: string }>()
);

export const loadEquipment = createAction(
	"[Character] Load Equipment",
	props<{ characterId: string }>()
);

export const loadEquipmentSuccess = createAction(
	"[Character] Load Equipment Success",
	props<{ equipment: Equipment }>()
);

export const loadEquipmentFailure = createAction(
	"[Character] Load Equipment Failure",
	props<{ error: string }>()
);

export const loadSocketStatus = createAction(
	"[Character] Load Socket Status",
	props<{ characterId: string }>()
);

export const loadSocketStatusSuccess = createAction(
	"[Character] Load Socket Status Success",
	props<{ socketStatus: EquipmentSocketStatus }>()
);

export const loadSocketStatusFailure = createAction(
	"[Character] Load Socket Status Failure",
	props<{ error: string }>()
);

export const loadBuffs = createAction(
	"[Character] Load Buffs",
	props<{ characterId: string }>()
);

export const loadBuffListSuccess = createAction(
	"[Character] Load Buff List Success",
	props<{ buffListType: BuffListType, buffList: Buff[] }>()
);

export const loadBuffListFailure = createAction(
	"[Character] Load Buff List Failure",
	props<{ buffListType: BuffListType, error: string }>()
);

export const loadConsumables = createAction(
	"[Character] Load Consumables",
	props<{ characterId: string }>()
);

export const loadConsumablesSuccess = createAction(
	"[Character] Load Consumables Success",
	props<{ consumables: Consumable[] }>()
);

export const loadConsumablesFailure = createAction(
	"[Character] Load Consumables Failure",
	props<{ error: string }>()
);

export const equipItemBestVariant = createAction(
	"[Character] Equip Item Best Variant",
	props<{ characterId: string, itemSlot: ItemSlot, item: Item }>()
);

export const equipItemBestVariantSuccess = createAction(
	"[Character] Equip Item Best Variant Success",
	props<{ characterId: string, itemSlot: ItemSlot, equippableItem: EquippableItem }>()
);

export const equipItemBestVariantFailure = createAction(
	"[Character] Equip Item Best Variant Failure",
	props<{ itemSlot: ItemSlot, error: string }>()
);

export const equipItemGroup = createAction(
	"[Character] Equip Item Group",
	props<{ characterId: string, slotGroup: ItemSlotGroup, items: EquippableItem[] }>()
);

export const equipItemGroupSuccess = createAction(
	"[Character] Equip Item Group Success",
	props<{ characterId: string, slotGroup: ItemSlotGroup, items: EquippableItem[] }>()
);

export const equipItemGroupFailure = createAction(
	"[Character] Equip Item Group Failure",
	props<{ slotGroup: ItemSlotGroup, error: string }>()
);

export const equipEnchant = createAction(
	"[Character] Equip Enchant",
	props<{ characterId: string, equippedItem: EquippableItem, itemSlot: ItemSlot, enchant: Enchant }>()
);

export const equipEnchantSuccess = createAction(
	"[Character] Equip Enchant Success",
	props<{ characterId: string, itemSlot: ItemSlot, equippableItem: EquippableItem }>()
);

export const equipEnchantFailure = createAction(
	"[Character] Equip Enchant Failure",
	props<{ itemSlot: ItemSlot, error: string }>()
);

export const equipGem = createAction(
	"[Character] Equip Gem",
	props<{ characterId: string, equippedItem: EquippableItem, itemSlot: ItemSlot, socketNo: number, gem: Gem }>()
);

export const equipGemSuccess = createAction(
	"[Character] Equip Gem Success",
	props<{ characterId: string, itemSlot: ItemSlot, socketNo: number, equippableItem: EquippableItem }>()
);

export const equipGemFailure = createAction(
	"[Character] Equip Gem Failure",
	props<{ itemSlot: ItemSlot, socketNo: number, error: string }>()
);

export const resetEquipment = createAction(
	"[Character] Reset Equipment",
	props<{ characterId: string }>()
);

export const resetEquipmentSuccess = createAction(
	"[Character] Reset Equipment Success",
	props<{ characterId: string }>()
);

export const resetEquipmentFailure = createAction(
	"[Character] Reset Equipment Failure",
	props<{ error: string }>()
);

export const enableBuff = createAction(
	"[Character] EnableBuff",
	props<{ characterId: string, buffListType: BuffListType, buff: Buff }>()
);

export const enableBuffSuccess = createAction(
	"[Character] EnableBuff Success",
	props<{ characterId: string, buffListType: BuffListType, buffList: Buff[] }>()
);

export const enableBuffFailure = createAction(
	"[Character] EnableBuff Failure",
	props<{ buffListType: BuffListType, error: string }>()
);

export const enableConsumable = createAction(
	"[Character] Enable Consumable",
	props<{ characterId: string, consumable: Consumable }>()
);

export const enableConsumableSuccess = createAction(
	"[Character] Enable Consumable Success",
	props<{ characterId: string, consumables: Consumable[] }>()
);

export const enableConsumableFailure = createAction(
	"[Character] Enable Consumable Failure",
	props<{ error: string }>()
);

export const dpsChanged = createAction(
	"[Character] Dps Changed",
	props<{ characterId: string }>()
);
