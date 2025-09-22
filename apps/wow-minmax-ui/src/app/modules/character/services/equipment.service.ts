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
import { ItemSlotGroup } from '../model/upgrade/ItemSlotGroup';

@Injectable({
	providedIn: 'root'
})
export class EquipmentService {
	private readonly apiUrl = environment.equipmentApiUrl;

	constructor(private http: HttpClient) {}

	getEquipment(characterId: string) {
		return this.http.get<Equipment>(`${this.apiUrl}/${characterId}`);
	}

	equipItemBestVariant(characterId: string, itemSlot: ItemSlot, item: Item) {
		var equippableItem: EquippableItem = {
			item,
			enchant: null,
			gems: []
		};
		return this.http.put<EquippableItem>(`${this.apiUrl}/${characterId}/slot/${itemSlot}?best-variant=true`, equippableItem);
	}

	equipItem(characterId: string, itemSlot: ItemSlot, item: EquippableItem | null) {
		return this.http.put<EquippableItem>(`${this.apiUrl}/${characterId}/slot/${itemSlot}`, item);
	}

	equipEnchant(characterId: string, item: EquippableItem, itemSlot: ItemSlot, enchant: Enchant | null) {
		const newItem = {
			...item,
			enchant
		};
		return this.equipItem(characterId, itemSlot, newItem);
	}

	equipGem(characterId: string, item: EquippableItem, itemSlot: ItemSlot, socketNo: number, gem: Gem | null) {
		const newItem = {
			...item,
			gems: [ ...item.gems ]
		};
		newItem.gems[socketNo] = gem;
		return this.equipItem(characterId, itemSlot, newItem);
	}

	equipItems(characterId: string, slotGroup: ItemSlotGroup, items: EquippableItem[]) {
		return this.http.put<void>(`${this.apiUrl}/${characterId}/slot-group/${slotGroup}`, items);
	}

	resetEquipment(characterId: string) {
		return this.http.delete<void>(`${this.apiUrl}/${characterId}`);
	}

	getAvailableGearSets(characterId: string) {
		return this.http.get<string[]>(`${this.apiUrl}/${characterId}/gear-set`);
	}

	equipGearSet(characterId: string, gearSet: string) {
		return this.http.get<Equipment>(`${this.apiUrl}/${characterId}/gear-set/${gearSet}/equip`);
	}

	getSocketStatus(characterId: string) {
		return this.http.get<EquipmentSocketStatus>(`${this.apiUrl}/${characterId}/socket-status`);
	}
}
