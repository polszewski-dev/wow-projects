import { Component, Input, OnInit } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../../model/upgrade/Upgrade';
import { CharacterStateService } from '../../services/character-state.service';
import { UpgradeStateService } from '../../services/upgrade-state.service';

@Component({
	selector: 'app-equip-upgrade-button',
	templateUrl: './equip-upgrade-button.component.html',
	styleUrls: ['./equip-upgrade-button.component.css']
})
export class EquipUpgradeButtonComponent implements OnInit {
	@Input({ required: true }) slotGroup!: ItemSlotGroup;

	upgrade$!: Observable<Upgrade | undefined>;

	constructor(
		private characterStateService: CharacterStateService,
		private upgradeStateService: UpgradeStateService
	) {}

	ngOnInit(): void {
		this.upgrade$ = this.upgradeStateService.upgrade$(this.slotGroup).pipe(
			map(upgrades => upgrades?.[0])
		);
	}

	onEquipUpgradeClick(upgrade: Upgrade) {
		const items = upgrade.itemDifference;
		this.characterStateService.equipItemGroup(this.slotGroup, items);
	}

	getUpgradeLevel(changePct: number) {
		return changePct > 10 ? 5 :
			changePct > 5 ? 4 :
			changePct > 3 ? 3 :
			changePct > 1 ? 2 :
			1;
	}
}
