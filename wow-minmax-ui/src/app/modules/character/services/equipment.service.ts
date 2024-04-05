import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EnchantOptions } from '../model/equipment/EnchantOptions';
import { Equipment } from '../model/equipment/Equipment';
import { EquipmentOptions } from '../model/equipment/EquipmentOptions';
import { EquipmentSocketStatus } from '../model/equipment/EquipmentSocketStatus';
import { EquippableItem } from '../model/equipment/EquippableItem';
import { GemOptions } from '../model/equipment/GemOptions';
import { ItemOptions } from '../model/equipment/ItemOptions';
import { ItemSlot } from '../model/equipment/ItemSlot';
import { ItemSlotGroup } from '../model/upgrade/ItemSlotGroup';

@Injectable({
	providedIn: 'root'
})
export class EquipmentService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/equipment';

	constructor(private http: HttpClient) {}

	getEquipment(characterId: string) {
		return this.http.get<Equipment>(`${this.apiUrl}/${characterId}`);
	}

	getEquipmentOptions(characterId: string) {
		return this.http.get<EquipmentOptions>(`${this.apiUrl}/${characterId}/options`);
	}

	getItemOptions(characterId: string, itemSlot: ItemSlot) {
		return this.http.get<ItemOptions>(`${this.apiUrl}/${characterId}/options/item/${itemSlot}`);
	}

	getEnchantOptions(characterId: string) {
		return this.http.get<EnchantOptions[]>(`${this.apiUrl}/${characterId}/options/enchant`);
	}

	getGemOptions(characterId: string) {
		return this.http.get<GemOptions[]>(`${this.apiUrl}/${characterId}/options/gem`);
	}

	changeItemBestVariant(characterId: string, itemSlot: ItemSlot, itemId: number) {
		return this.http.get<EquippableItem>(`${this.apiUrl}/${characterId}/change/item/${itemSlot}/${itemId}/best/variant`);
	}

	changeItem(characterId: string, itemSlot: ItemSlot, item?: EquippableItem) {
		return this.http.put<void>(`${this.apiUrl}/${characterId}/change/item/${itemSlot}`, item);
	}

	changeItems(characterId: string, slotGroup: ItemSlotGroup, items: EquippableItem[]) {
		return this.http.put<void>(`${this.apiUrl}/${characterId}/change/item/group/${slotGroup}`, items);
	}

	resetEquipment(characterId: string) {
		return this.http.get<void>(`${this.apiUrl}/${characterId}/reset`);
	}

	getSocketStatus(characterId: string) {
		return this.http.get<EquipmentSocketStatus>(`${this.apiUrl}/${characterId}/socket/status`);
	}
}
