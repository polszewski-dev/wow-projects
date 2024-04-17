import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EnchantOptions } from '../model/equipment/EnchantOptions';
import { EquipmentOptions } from '../model/equipment/EquipmentOptions';
import { GemOptions } from '../model/equipment/GemOptions';
import { ItemOptions } from '../model/equipment/ItemOptions';
import { ItemSlot } from '../model/equipment/ItemSlot';

@Injectable({
	providedIn: 'root'
})
export class EquipmentOptionsService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/equipment-options';

	constructor(private http: HttpClient) {}

	getEquipmentOptions(characterId: string) {
		return this.http.get<EquipmentOptions>(`${this.apiUrl}/${characterId}`);
	}

	getItemOptions(characterId: string, itemSlot: ItemSlot) {
		return this.http.get<ItemOptions>(`${this.apiUrl}/${characterId}/item/${itemSlot}`);
	}

	getEnchantOptions(characterId: string) {
		return this.http.get<EnchantOptions[]>(`${this.apiUrl}/${characterId}/enchant`);
	}

	getGemOptions(characterId: string) {
		return this.http.get<GemOptions[]>(`${this.apiUrl}/${characterId}/gem`);
	}
}
