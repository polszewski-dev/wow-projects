import { Component } from '@angular/core';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { ItemSlotGroup } from 'src/app/model/upgrade/ItemSlotGroup';
import { Upgrade } from 'src/app/model/upgrade/Upgrade';
import { StatsService } from 'src/app/services/stats.service';
import { UpgradeService } from 'src/app/services/upgrade.service';

@Component({
	selector: 'app-profile-editor',
	templateUrl: './profile-editor.component.html',
	styleUrls: ['./profile-editor.component.css']
})
export class ProfileEditorComponent {
	selectedProfile!: ProfileInfo;
	dps?: number;
	previousDps?: number;
	upgradesBySlotGroup: { [key in ItemSlotGroup]?: Upgrade[] } = {};

	constructor(private statsService: StatsService, private upgradeService: UpgradeService) {}

	onProfileSelected(selectedProfile: ProfileInfo): void {
		this.selectedProfile = selectedProfile;
		this.dps = undefined;
		this.updateDps();
		this.upgradesBySlotGroup = {};
		this.updateUpgradeStatus();
	}

	onEquipmentChanged(): void {
		this.updateDps();
		this.updateUpgradeStatus();
	}

	onBuffsChanged(): void {
		this.updateDps();
	}

	updateDps(): void {
		this.statsService.getSpellDps(this.selectedProfile.profileId).subscribe((dps: number) => {
			this.previousDps = this.dps;
			this.dps = dps;
		})
	}

	updateUpgradeStatus(): void {
		for (let slotGroup of Object.values(ItemSlotGroup)) {
			this.upgradeService.getUpgrades(this.selectedProfile.profileId, slotGroup).subscribe((upgrades: Upgrade[]) => {
				this.upgradesBySlotGroup[slotGroup] = upgrades;
			});
		}
	}
}
