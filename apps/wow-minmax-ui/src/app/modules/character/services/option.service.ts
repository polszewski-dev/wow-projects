import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { BuffGroup, BuffStatus } from '../model/buff/BuffStatus';
import { ConsumableGroup, ConsumableStatus } from '../model/consumable/ConsumableStatus';

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
}
