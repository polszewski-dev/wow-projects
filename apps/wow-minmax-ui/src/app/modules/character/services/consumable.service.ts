import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ConsumableStatus } from '../model/consumable/ConsumableStatus';

@Injectable({
	providedIn: 'root'
})
export class ConsumableService {
	private readonly apiUrl = environment.consumableApiUrl;

	constructor(private http: HttpClient) { }

	getConsumableStatuses(characterId: string) {
		return this.http.get<ConsumableStatus[]>(`${this.apiUrl}/${characterId}`);
	}

	changeConsumableStatus(characterId: string, consumable: ConsumableStatus) {
		return this.http.put<ConsumableStatus[]>(`${this.apiUrl}/${characterId}`, consumable);
	}
}
