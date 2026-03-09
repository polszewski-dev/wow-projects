import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Enchant } from '../model/equipment/Enchant';
import { Equipment } from '../model/equipment/Equipment';
import { EquipmentSocketStatus } from '../model/equipment/EquipmentSocketStatus';
import { EquippableItem } from '../model/equipment/EquippableItem';
import { Gem } from '../model/equipment/Gem';
import { Item } from '../model/equipment/Item';
import { ItemSlot } from '../model/equipment/ItemSlot';
import { EquipmentDiff } from '../model/equipment/ItemSlotStatus';
import { ItemSlotGroup } from '../model/upgrade/ItemSlotGroup';

@Injectable({
	providedIn: 'root'
})
export class EquipmentService {
	private readonly apiUrl = environment.equipmentApiUrl;

	constructor(private http: HttpClient) {}

	getEquipment(playerId: string) {
		return this.http.get<Equipment>(`${this.apiUrl}/${playerId}`);
	}

	equipItemBestVariant(playerId: string, itemSlot: ItemSlot, item: Item) {
		var equippableItem: EquippableItem = {
			item,
			enchant: null,
			gems: []
		};
		return this.http.put<EquipmentDiff>(`${this.apiUrl}/${playerId}/slot/${itemSlot}?best-variant=true`, equippableItem);
	}

	equipItem(playerId: string, itemSlot: ItemSlot, item: EquippableItem | null) {
		return this.http.put<EquipmentDiff>(`${this.apiUrl}/${playerId}/slot/${itemSlot}`, item);
	}

	equipEnchant(playerId: string, item: EquippableItem, itemSlot: ItemSlot, enchant: Enchant | null) {
		const newItem = {
			...item,
			enchant
		};
		return this.equipItem(playerId, itemSlot, newItem);
	}

	equipGem(playerId: string, item: EquippableItem, itemSlot: ItemSlot, socketNo: number, gem: Gem | null) {
		const newItem = {
			...item,
			gems: [ ...item.gems ]
		};
		newItem.gems[socketNo] = gem;
		return this.equipItem(playerId, itemSlot, newItem);
	}

	equipItems(playerId: string, slotGroup: ItemSlotGroup, items: EquippableItem[]) {
		return this.http.put<EquipmentDiff>(`${this.apiUrl}/${playerId}/slot-group/${slotGroup}`, items);
	}

	resetEquipment(playerId: string) {
		return this.http.delete<void>(`${this.apiUrl}/${playerId}`);
	}

	getAvailableGearSets(playerId: string) {
		return this.http.get<string[]>(`${this.apiUrl}/${playerId}/gear-set`);
	}

	equipGearSet(playerId: string, gearSet: string) {
		return this.http.get<Equipment>(`${this.apiUrl}/${playerId}/gear-set/${gearSet}/equip`);
	}

	equipPreviousPhase(playerId: string) {
		return this.http.get<Equipment>(`${this.apiUrl}/${playerId}/equip-previous-phase`);
	}

	getSocketStatus(playerId: string) {
		return this.http.get<EquipmentSocketStatus>(`${this.apiUrl}/${playerId}/socket-status`);
	}
}
