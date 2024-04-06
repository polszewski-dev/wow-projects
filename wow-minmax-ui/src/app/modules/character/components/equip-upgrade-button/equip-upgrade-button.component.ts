import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../../model/upgrade/Upgrade';

@Component({
	selector: 'app-equip-upgrade-button',
	templateUrl: './equip-upgrade-button.component.html',
	styleUrls: ['./equip-upgrade-button.component.css']
})
export class EquipUpgradeButtonComponent {
	@Input() slotGroup?: ItemSlotGroup;
	@Input() upgrades?: Upgrade[];
	@Output() clicked = new EventEmitter<ItemSlotGroup>();

	onEquipUpgradeClicked() {
		this.clicked.emit(this.slotGroup);
	}

	getUpgradeLevel(changePct: number) {
		return changePct > 10 ? 5 :
			changePct > 5 ? 4 :
			changePct > 3 ? 3 :
			changePct > 1 ? 2 :
			1;
	}
}
