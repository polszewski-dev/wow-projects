import { createAction, props } from "@ngrx/store";
import { BuffListType } from '../../model/buff/BuffListType';
import { BuffStatus } from "../../model/buff/BuffStatus";
import { Character } from '../../model/Character';
import { ConsumableStatus } from "../../model/consumable/ConsumableStatus";
import { Enchant } from "../../model/equipment/Enchant";
import { Equipment } from '../../model/equipment/Equipment';
import { EquipmentSocketStatus } from '../../model/equipment/EquipmentSocketStatus';
import { EquippableItem } from '../../model/equipment/EquippableItem';
import { Gem } from "../../model/equipment/Gem";
import { Item } from "../../model/equipment/Item";
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { Profession } from "../../model/Profession";
import { ScriptInfo } from '../../model/ScriptInfo';
import { ItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';

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
	"[Character] Load Buff Status List Success",
	props<{ buffListType: BuffListType, buffStatusList: BuffStatus[] }>()
);

export const loadBuffListFailure = createAction(
	"[Character] Load Buff List Failure",
	props<{ buffListType: BuffListType, error: string }>()
);

export const loadConsumableStatuses = createAction(
	"[Character] Load Consumable Statuses",
	props<{ characterId: string }>()
);

export const loadConsumableStatusesSuccess = createAction(
	"[Character] Load Consumable Statuses Success",
	props<{ consumableStatuses: ConsumableStatus[] }>()
);

export const loadConsumableStatusesFailure = createAction(
	"[Character] Load Consumable Statuses Failure",
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

export const equipGearSet = createAction(
	"[Character] Equip Gear Set",
	props<{ characterId: string, gearSet: string }>()
);

export const equipGearSetSuccess = createAction(
	"[Character] Equip Gear Set Success",
	props<{ characterId: string, equipment: Equipment }>()
);

export const equipGearSetFailure = createAction(
	"[Character] Equip Gear Set Failure",
	props<{ error: string }>()
);

export const equipPreviousPhase = createAction(
	"[Character] Equip Previous Phase",
	props<{ characterId: string }>()
);

export const equipPreviousPhaseSuccess = createAction(
	"[Character] Equip Previous Phase Success",
	props<{ characterId: string, equipment: Equipment }>()
);

export const equipPreviousPhaseFailure = createAction(
	"[Character] Equip Previous Phase Failure",
	props<{ error: string }>()
);

export const changeBuffStatus = createAction(
	"[Character] Change Buff Status",
	props<{ characterId: string, buffListType: BuffListType, buffStatus: BuffStatus }>()
);

export const changeBuffStatusSuccess = createAction(
	"[Character] Change Buff Status Success",
	props<{ characterId: string, buffListType: BuffListType, buffStatusList: BuffStatus[] }>()
);

export const changeBuffStatusFailure = createAction(
	"[Character] Change Buff Status Failure",
	props<{ buffListType: BuffListType, error: string }>()
);

export const changeConsumableStatus = createAction(
	"[Character] Change Consumable Status",
	props<{ characterId: string, consumableStatus: ConsumableStatus }>()
);

export const changeConsumableStatusSuccess = createAction(
	"[Character] Change Consumable Status Success",
	props<{ characterId: string, consumableStatuses: ConsumableStatus[] }>()
);

export const changeConsumableStatusFailure = createAction(
	"[Character] Change Consumable Status Failure",
	props<{ error: string }>()
);

export const dpsChanged = createAction(
	"[Character] Dps Changed",
	props<{ characterId: string }>()
);

export const changeProfession = createAction(
	"[Character] Change Profession",
	props<{ characterId: string, professionIdx: number, profession: Profession }>()
);

export const changeProfessionSuccess = createAction(
	"[Character] Change Profession Success",
	props<{ characterId: string, character: Character }>()
);

export const changeProfessionFailure = createAction(
	"[Character] Change Profession Failure",
	props<{ error: string }>()
);

export const changeScript = createAction(
	"[Character] Change Script",
	props<{ characterId: string, script: ScriptInfo }>()
);

export const changeScriptSuccess = createAction(
	"[Character] Change Script Success",
	props<{ characterId: string, character: Character }>()
);

export const changeScriptFailure = createAction(
	"[Character] Change Script Failure",
	props<{ error: string }>()
);
