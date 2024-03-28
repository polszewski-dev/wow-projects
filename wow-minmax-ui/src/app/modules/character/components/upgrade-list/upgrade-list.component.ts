import { Component, Input } from '@angular/core';
import { ItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../../model/upgrade/Upgrade';

@Component({
	selector: 'app-upgrade-list',
	templateUrl: './upgrade-list.component.html',
	styleUrls: ['./upgrade-list.component.css']
})
export class UpgradeListComponent {
	@Input() upgradesBySlotGroup: Partial<Record<ItemSlotGroup, Upgrade[]>> = {};
	visible = false;

	readonly itemSlotGroups = Object.values(ItemSlotGroup);

	hasAnyUpgrades() {
		return Object.values(this.upgradesBySlotGroup).some(x => x.length > 0);
	}

	toggleVisibility() {
		this.visible = !this.visible;
	}
}
