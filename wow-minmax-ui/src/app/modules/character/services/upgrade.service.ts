import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ItemFilter } from '../model/equipment/ItemFilter';
import { ItemSlotGroup } from '../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../model/upgrade/Upgrade';

@Injectable({
	providedIn: 'root'
})
export class UpgradeService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/upgrade';

	constructor(private http: HttpClient) { }

	getUpgrades(characterId: string, slotGroup: ItemSlotGroup, itemFilter: ItemFilter) {
		return this.http.get<Upgrade[]>(`${this.apiUrl}/${characterId}/slot/${slotGroup}?${this.getFilterString(itemFilter)}`);
	}

	private getFilterString(itemFilter: ItemFilter) {
		return Object.entries(itemFilter).map(x => x[0] + '=' + x[1]).join('&');
	}
}
