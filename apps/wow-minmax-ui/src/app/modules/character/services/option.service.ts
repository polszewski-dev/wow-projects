import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { AssetGroup, AssetStatus } from '../model/Asset';
import { BuffGroup, BuffStatus } from '../model/Buff';
import { ConsumableGroup, ConsumableStatus } from '../model/Consumable';

@Injectable({
	providedIn: 'root'
})
export class OptionService {
	private readonly apiUrl = environment.optionApiUrl;

	constructor(private http: HttpClient) { }

	getBuffStatuses(playerId: string) {
		return this.http.get<BuffGroup[]>(`${this.apiUrl}/${playerId}/buffs`);
	}

	changeBuffStatus(playerId: string, buffStatus: BuffStatus) {
		return this.http.put<void>(`${this.apiUrl}/${playerId}/buffs`, buffStatus);
	}

	getConsumableStatuses(playerId: string) {
		return this.http.get<ConsumableGroup[]>(`${this.apiUrl}/${playerId}/consumables`);
	}

	changeConsumableStatus(playerId: string, consumable: ConsumableStatus) {
		return this.http.put<void>(`${this.apiUrl}/${playerId}/consumables`, consumable);
	}

	getAssetStatuses(playerId: string) {
		return this.http.get<AssetGroup[]>(`${this.apiUrl}/${playerId}/assets`);
	}

	changeAssetStatus(playerId: string, assetStatus: AssetStatus) {
		return this.http.put<void>(`${this.apiUrl}/${playerId}/assets`, assetStatus);
	}
}
