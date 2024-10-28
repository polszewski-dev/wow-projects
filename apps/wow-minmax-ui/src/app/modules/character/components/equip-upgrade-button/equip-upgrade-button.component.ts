import { Component, Input, OnInit } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { ItemSlotGroup } from '../../model/upgrade/ItemSlotGroup';
import { Upgrade } from '../../model/upgrade/Upgrade';
import { CharacterModuleState } from '../../state/character-module.state';
import { equipItemGroup } from '../../state/character/character.actions';
import { selectCharacterId } from '../../state/character/character.selectors';
import { selectUpgrades } from '../../state/upgrades/upgrades.selectors';

@Component({
	selector: 'app-equip-upgrade-button',
	templateUrl: './equip-upgrade-button.component.html',
	styleUrls: ['./equip-upgrade-button.component.css']
})
export class EquipUpgradeButtonComponent implements OnInit {
	@Input({ required: true }) slotGroup!: ItemSlotGroup;

	data$!: Observable<DataView>;

	constructor(private store: Store<CharacterModuleState>) {}

	ngOnInit(): void {
		this.data$ = this.store.select(createDataSelector(this.slotGroup));
	}

	onEquipUpgradeClick(characterId: string, upgrade: Upgrade) {
		const items = upgrade.itemDifference;
		this.store.dispatch(equipItemGroup({ characterId, slotGroup: this.slotGroup, items }));
	}

	getUpgradeLevel(changePct: number) {
		return changePct > 10 ? 5 :
			changePct > 5 ? 4 :
			changePct > 3 ? 3 :
			changePct > 1 ? 2 :
			1;
	}
}

type DataView = {
	characterId: string;
	upgrade: Upgrade;
} | null;

function createDataSelector(slotGroup: ItemSlotGroup) {
	return createSelector(
		selectCharacterId,
		selectUpgrades(slotGroup),
		(characterId, upgrades): DataView => {
			if (!characterId) {
				return null;
			}

			return {
				characterId: characterId!,
				upgrade: upgrades[0] || null
			};
		}
	);
}
