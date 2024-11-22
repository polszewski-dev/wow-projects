import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Consumable } from '../model/consumable/Consumable';

@Injectable({
	providedIn: 'root'
})
export class ConsumableService {
	private readonly apiUrl = environment.consumableApiUrl;

	constructor(private http: HttpClient) { }

	getConsumables(characterId: string) {
		return this.http.get<Consumable[]>(`${this.apiUrl}/${characterId}`);
	}

	enableConsumable(characterId: string, consumable: Consumable) {
		return this.http.put<Consumable[]>(`${this.apiUrl}/${characterId}`, consumable);
	}
}
