import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { ItemFilter } from 'src/app/model/equipment/ItemFilter';
import { ItemSlotGroup } from 'src/app/model/upgrade/ItemSlotGroup';
import { Upgrade } from 'src/app/model/upgrade/Upgrade';
import { StatsService } from 'src/app/services/stats.service';
import { UpgradeService } from 'src/app/services/upgrade.service';

@Component({
	selector: 'app-profile-editor',
	templateUrl: './profile-editor.component.html',
	styleUrls: ['./profile-editor.component.css']
})
export class ProfileEditorComponent implements OnChanges {
	@Input() selectedCharacterId!: string;
	dps?: number;
	previousDps?: number;
	upgradesBySlotGroup: { [key in ItemSlotGroup]?: Upgrade[] } = {};

	itemFilter: ItemFilter = {
		heroics: true,
		raids: true,
		worldBosses: false,
		healingItems: false,
		pvpItems: false,
		greens: true,
		legendaries: false
	};

	constructor(private statsService: StatsService, private upgradeService: UpgradeService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.dps = undefined;
		this.updateDps();
		this.upgradesBySlotGroup = {};
		this.updateUpgradeStatus();
	}

	onEquipmentChanged(): void {
		this.updateDps();
		this.updateUpgradeStatus();
	}

	onItemFilterChanged(): void {
		this.updateUpgradeStatus();
	}

	onBuffsChanged(): void {
		this.updateDps();
	}

	updateDps(): void {
		this.statsService.getSpellDps(this.selectedCharacterId!).subscribe((dps: number) => {
			this.previousDps = this.dps;
			this.dps = dps;
		})
	}

	updateUpgradeStatus(): void {
		for (let slotGroup of Object.values(ItemSlotGroup)) {
			this.upgradeService.getUpgrades(this.selectedCharacterId!, slotGroup, this.itemFilter).subscribe((upgrades: Upgrade[]) => {
				this.upgradesBySlotGroup[slotGroup] = upgrades;
			});
		}
	}
}
