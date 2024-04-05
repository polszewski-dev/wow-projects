import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ItemFilter } from '../model/equipment/ItemFilter';
import { ItemSlotGroup } from '../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../model/upgrade/Upgrade';
import { UpgradeService } from './upgrade.service';

@Injectable({
	providedIn: 'root'
})
export class UpgradeStateService {
	private readonly allUpgradesSubject = new BehaviorSubject<AllUpgrades>({});
	private readonly upgradeSubjects = this.createUpgradeSubjects();

	readonly allUpgrades$ = this.allUpgradesSubject.asObservable();

	upgrade$(itemSlotGroup: ItemSlotGroup) {
		return this.upgradeSubjects[itemSlotGroup];
	}

	private allUpgrades: AllUpgrades = {};

	private characterId: string | undefined;

	private itemFilter: ItemFilter = {
		heroics: true,
		raids: true,
		worldBosses: false,
		pvpItems: false,
		greens: true,
		legendaries: false
	};

	get itemFilterSnapshot() {
		return this.itemFilter;
	}

	constructor(private upgradeService: UpgradeService) {}

	clearUpgrades() {
		this.allUpgrades = {};
		this.allUpgradesSubject.next(this.allUpgrades);

		Object.values(this.upgradeSubjects).forEach(subject => subject.next(undefined));
	}

	loadUpgrades(characterId: string) {
		this.characterId = characterId;

		Object.keys(this.upgradeSubjects).forEach(key => {
			const slotGroup = key as ItemSlotGroup;
			this.upgradeService.getUpgrades(characterId, slotGroup, this.itemFilter).subscribe(upgrades => {
				this.allUpgrades[slotGroup] = upgrades;
				this.allUpgradesSubject.next(this.allUpgrades);
				this.upgradeSubjects[slotGroup].next(upgrades);
			})
		});
	}

	updateItemFilter(itemFilter: Partial<ItemFilter>) {
		Object.keys(itemFilter).forEach(k => {
			const key = k as keyof ItemFilter;
			const value = itemFilter[key];
			if (value !== undefined) {
				this.itemFilter[key] = value;
			}
		});

		this.loadUpgrades(this.characterId!);
	}

	private createUpgradeSubjects() {
		return Object.fromEntries(
			Object.values(ItemSlotGroup).map(k => [k as ItemSlotGroup, new BehaviorSubject<Upgrade[] | undefined>(undefined)])
		);
	}
}

type AllUpgrades = Partial<Record<ItemSlotGroup, Upgrade[]>>;
