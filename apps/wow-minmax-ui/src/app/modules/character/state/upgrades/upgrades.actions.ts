import { createAction, props } from "@ngrx/store";
import { ItemFilter } from '../../model/equipment/ItemFilter';
import { ItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';
import { Upgrade } from "../../model/upgrade/Upgrade";

export const updateItemFilter = createAction(
	"[Upgrades] Update Item Filter",
	props<{ characterId: string, itemFilter: Partial<ItemFilter> }>()
);

export const loadAllUpgrades = createAction(
	"[Upgrades] Load All Upgrades",
	props<{ characterId: string }>()
);

export const loadAllUpgradesSuccess = createAction(
	"[Upgrades] Load All Upgrades Success",
	props<{ slotGroup: ItemSlotGroup, upgrades: Upgrade[] }>()
);

export const loadAllUpgradesFailure = createAction(
	"[Upgrades] Load All Failure",
	props<{ slotGroup: ItemSlotGroup, error: string }>()
);

