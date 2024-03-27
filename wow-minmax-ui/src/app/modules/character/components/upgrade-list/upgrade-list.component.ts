import { Component, Input } from '@angular/core';
import { ItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../../model/upgrade/Upgrade';

@Component({
	selector: 'app-upgrade-list',
	templateUrl: './upgrade-list.component.html',
	styleUrls: ['./upgrade-list.component.css']
})
export class UpgradeListComponent {
	@Input() upgradesBySlotGroup: { [key in ItemSlotGroup]?: Upgrade[] } = {};
	visible=false;

	readonly itemSlotGroups: ItemSlotGroup[] = Object.values(ItemSlotGroup);

	hasAnyUpgrades(): boolean {
		return Object.values(this.upgradesBySlotGroup).some(x => x.length > 0);
	}

	toggleVisibility(): void {
		this.visible = !this.visible;
	}
}
