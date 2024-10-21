import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Equipment } from '../model/equipment/Equipment';
import { EquipmentSocketStatus } from '../model/equipment/EquipmentSocketStatus';
import { EquippableItem } from '../model/equipment/EquippableItem';
import { ItemSlot } from '../model/equipment/ItemSlot';
import { ItemSlotGroup } from '../model/upgrade/ItemSlotGroup';
import { Item } from '../model/equipment/Item';
import { environment } from 'src/environments/environment';

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
			gems: []
		};
		return this.http.put<EquippableItem>(`${this.apiUrl}/${characterId}/slot/${itemSlot}?best-variant=true`, equippableItem);
	}

	equipItem(characterId: string, itemSlot: ItemSlot, item?: EquippableItem) {
		return this.http.put<EquippableItem>(`${this.apiUrl}/${characterId}/slot/${itemSlot}`, item);
	}

	equipItems(characterId: string, slotGroup: ItemSlotGroup, items: EquippableItem[]) {
		return this.http.put<void>(`${this.apiUrl}/${characterId}/slot-group/${slotGroup}`, items);
	}

	resetEquipment(characterId: string) {
		return this.http.delete<void>(`${this.apiUrl}/${characterId}`);
	}

	getSocketStatus(characterId: string) {
		return this.http.get<EquipmentSocketStatus>(`${this.apiUrl}/${characterId}/socket-status`);
	}
}
