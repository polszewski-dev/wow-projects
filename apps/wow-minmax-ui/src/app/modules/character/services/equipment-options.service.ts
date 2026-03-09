import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EnchantOptions } from '../model/equipment/EnchantOptions';
import { EquipmentOptions } from '../model/equipment/EquipmentOptions';
import { GemOptions } from '../model/equipment/GemOptions';
import { ItemOptions } from '../model/equipment/ItemOptions';
import { ItemSlot } from '../model/equipment/ItemSlot';
import { environment } from 'src/environments/environment';

@Injectable({
	providedIn: 'root'
})
export class EquipmentOptionsService {
	private readonly apiUrl = environment.equipmentOptionApiUrl;

	constructor(private http: HttpClient) {}

	getEquipmentOptions(playerId: string) {
		return this.http.get<EquipmentOptions>(`${this.apiUrl}/${playerId}`);
	}

	getItemOptions(playerId: string, itemSlot: ItemSlot) {
		return this.http.get<ItemOptions>(`${this.apiUrl}/${playerId}/item/${itemSlot}`);
	}

	getEnchantOptions(playerId: string) {
		return this.http.get<EnchantOptions[]>(`${this.apiUrl}/${playerId}/enchant`);
	}

	getGemOptions(playerId: string) {
		return this.http.get<GemOptions[]>(`${this.apiUrl}/${playerId}/gem`);
	}
}
