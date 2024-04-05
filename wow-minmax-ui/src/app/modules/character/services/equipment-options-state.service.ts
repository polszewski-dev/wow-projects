import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { EnchantOptions } from '../model/equipment/EnchantOptions';
import { EquipmentOptions } from '../model/equipment/EquipmentOptions';
import { GemOptions } from '../model/equipment/GemOptions';
import { ItemOptions } from '../model/equipment/ItemOptions';
import { ItemSlot } from '../model/equipment/ItemSlot';
import { EquipmentService } from './equipment.service';

@Injectable({
	providedIn: 'root'
})
export class EquipmentOptionsStateService {
	private readonly equipmentOptionsSubject = new BehaviorSubject<EquipmentOptions | undefined>(undefined);
	private readonly itemOptionsSubjects = this.createItemOptionsSubjects();
	private readonly enchantOptionsSubject = new BehaviorSubject<EnchantOptions[] | undefined>(undefined);
	private readonly gemOptionsSubject = new BehaviorSubject<GemOptions[] | undefined>(undefined);

	readonly equipmentOptions$ = this.equipmentOptionsSubject.asObservable();
	readonly enchantOptions$ = this.enchantOptionsSubject.asObservable();
	readonly gemOptions$ = this.gemOptionsSubject.asObservable();

	private equipmentOptions?: EquipmentOptions;

	itemOptionsByItemSlot$(itemSlot: ItemSlot) {
		return this.itemOptionsSubjects[itemSlot].asObservable();
	}

	get equipmentOptionsSnapshot() {
		return this.equipmentOptions;
	}

	constructor(private equipmentService: EquipmentService) { }

	clearOptions() {
		this.equipmentOptions = undefined;
		this.equipmentOptionsSubject.next(undefined);
		Object.values(this.itemOptionsSubjects).forEach(x => x.next(undefined));
		this.enchantOptionsSubject.next(undefined);
		this.gemOptionsSubject.next(undefined);
	}

	loadOptions(characterId: string) {
		this.equipmentService.getEquipmentOptions(characterId).subscribe(equipmentOptions => {
			this.equipmentOptions = equipmentOptions;
			this.equipmentOptionsSubject.next(equipmentOptions);
		});
		Object.keys(this.itemOptionsSubjects).forEach(itemSlot => {
			this.loadItemOptions(characterId, itemSlot as ItemSlot);
		});
		this.equipmentService.getEnchantOptions(characterId).subscribe(enchantOptions => {
			this.enchantOptionsSubject.next(enchantOptions);
		});
		this.equipmentService.getGemOptions(characterId).subscribe(gemOptions => {
			this.gemOptionsSubject.next(gemOptions);
		});
	}

	private loadItemOptions(characterId: string, itemSlot: ItemSlot) {
		if (itemSlot === ItemSlot.FINGER_2 || itemSlot === ItemSlot.TRINKET_2) {
			return;
		}

		this.equipmentService.getItemOptions(characterId, itemSlot).subscribe(itemOptions => {
			this.itemOptionsSubjects[itemSlot].next(itemOptions);
			if (itemSlot === ItemSlot.FINGER_1) {
				this.itemOptionsSubjects[ItemSlot.FINGER_2].next(itemOptions);
			} else if (itemSlot === ItemSlot.TRINKET_1) {
				this.itemOptionsSubjects[ItemSlot.TRINKET_2].next(itemOptions);
			}
		});
	}

	private createItemOptionsSubjects() {
		return Object.fromEntries(
			Object.values(ItemSlot).map(k => [k as ItemSlot, new BehaviorSubject<ItemOptions | undefined>(undefined)])
		);
	}
}
