import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ItemSlotGroup } from '../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../model/upgrade/Upgrade';

@Injectable({
	providedIn: 'root'
})
export class UpgradeService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/upgrade';

	constructor(private http: HttpClient) { }

	getUpgrades(characterId: string, slotGroup: ItemSlotGroup): Observable<Upgrade[]> {
		return this.http.get<Upgrade[]>(`${this.apiUrl}/${characterId}/slot/${slotGroup}`);
	}
}
