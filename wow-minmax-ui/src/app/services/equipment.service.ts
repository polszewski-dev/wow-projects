import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Equipment } from '../model/equipment/Equipment';
import { EquipmentOptions } from '../model/equipment/EquipmentOptions';
import { EquippableItem } from '../model/equipment/EquippableItem';
import { ItemSlot } from '../model/equipment/ItemSlot';
import { EquipmentSocketStatus } from '../model/equipment/EquipmentSocketStatus';
import { ItemSlotGroup } from '../model/upgrade/ItemSlotGroup';

@Injectable({
	providedIn: 'root'
})
export class EquipmentService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/equipment';

	constructor(private http: HttpClient) {}

	getEquipment(profileId: string): Observable<Equipment> {
		return this.http.get<Equipment>(`${this.apiUrl}/${profileId}`);
	}

	getEquipmentOptions(profileId: string): Observable<EquipmentOptions> {
		return this.http.get<EquipmentOptions>(`${this.apiUrl}/${profileId}/options`);
	}

	changeItemBestVariant(profileId: string, itemSlot: ItemSlot, itemId: number): Observable<EquippableItem> {
		return this.http.get<EquippableItem>(`${this.apiUrl}/${profileId}/change/item/${itemSlot}/${itemId}/best/variant`);
	}

	changeItem(profileId: string, itemSlot: ItemSlot, item: EquippableItem): Observable<EquippableItem> {
		return this.http.put<EquippableItem>(`${this.apiUrl}/${profileId}/change/item/${itemSlot}`, item);
	}

	changeItems(profileId: string, slotGroup: ItemSlotGroup, items: EquippableItem[]): Observable<void> {
		return this.http.put<void>(`${this.apiUrl}/${profileId}/change/item/group/${slotGroup}`, items);
	}

	resetEquipment(profileId: string): Observable<Equipment> {
		return this.http.get<Equipment>(`${this.apiUrl}/${profileId}/reset`);
	}

	getSocketStatus(profileId: string): Observable<EquipmentSocketStatus> {
		return this.http.get<EquipmentSocketStatus>(`${this.apiUrl}/${profileId}/socket/status`);
	}
}
