import { Component } from '@angular/core';
import { ItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../../model/upgrade/Upgrade';
import { UpgradeStateService } from '../../services/upgrade-state.service';

@Component({
	selector: 'app-upgrade-list',
	templateUrl: './upgrade-list.component.html',
	styleUrls: ['./upgrade-list.component.css']
})
export class UpgradeListComponent {
	allUpgrades$ = this.upgradeStateService.allUpgrades$;

	visible = false;

	readonly itemSlotGroups = Object.values(ItemSlotGroup);

	constructor(private upgradeStateService: UpgradeStateService) {}

	hasAnyUpgrades(allUpgrades: Partial<Record<ItemSlotGroup, Upgrade[]>>) {
		return Object.values(allUpgrades).some(x => x.length > 0);
	}

	toggleVisibility() {
		this.visible = !this.visible;
	}
}
