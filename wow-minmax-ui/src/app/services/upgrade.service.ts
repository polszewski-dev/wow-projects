import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ItemSlotGroup } from '../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../model/upgrade/Upgrade';
import { ItemFilter } from '../model/equipment/ItemFilter';

@Injectable({
	providedIn: 'root'
})
export class UpgradeService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/upgrade';

	constructor(private http: HttpClient) { }

	getUpgrades(characterId: string, slotGroup: ItemSlotGroup, itemFilter: ItemFilter): Observable<Upgrade[]> {
		return this.http.post<Upgrade[]>(`${this.apiUrl}/${characterId}/slot/${slotGroup}`, itemFilter);
	}
}
