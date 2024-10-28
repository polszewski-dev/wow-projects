import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { ItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';
import { CharacterModuleState } from '../../state/character-module.state';
import { selectAllUpgrades } from '../../state/upgrades/upgrades.selectors';

@Component({
	selector: 'app-upgrade-list',
	templateUrl: './upgrade-list.component.html',
	styleUrls: ['./upgrade-list.component.css']
})
export class UpgradeListComponent {
	allUpgrades$ = this.store.select(selectAllUpgrades);

	visible = false;

	constructor(private store: Store<CharacterModuleState>) {}

	toggleVisibility() {
		this.visible = !this.visible;
	}

	readonly itemSlotGroups = Object.values(ItemSlotGroup);
}
