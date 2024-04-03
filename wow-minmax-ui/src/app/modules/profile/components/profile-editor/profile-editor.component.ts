import { Component, Input, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { DpsBarComponent } from '../../../statistics/components/dps-bar/dps-bar.component';
import { Character } from '../../../character/model/Character';
import { BuffListType } from '../../../character/model/buff/BuffListType';
import { ItemFilter } from '../../../character/model/equipment/ItemFilter';
import { ItemSlotGroup } from '../../../character/model/upgrade/ItemSlotGroup';
import { Upgrade } from '../../../character/model/upgrade/Upgrade';
import { CharacterService } from '../../../character/services/character.service';
import { UpgradeService } from '../../../character/services/upgrade.service';

@Component({
	selector: 'app-profile-editor',
	templateUrl: './profile-editor.component.html',
	styleUrls: ['./profile-editor.component.css']
})
export class ProfileEditorComponent implements OnChanges {
	@Input() selectedCharacterId!: string;

	@ViewChild(DpsBarComponent, { static: false }) dpsBar!: DpsBarComponent;

	selectedCharacter?: Character;
	upgradesBySlotGroup: Partial<Record<ItemSlotGroup, Upgrade[]>> = {};

	itemFilter: ItemFilter = {
		heroics: true,
		raids: true,
		worldBosses: false,
		pvpItems: false,
		greens: true,
		legendaries: false
	};

	CHARACTER_BUFF = BuffListType.CHARACTER_BUFF;
	TARGET_DEBUFF = BuffListType.TARGET_DEBUFF;

	constructor(
			private characterService: CharacterService,
			private upgradeService: UpgradeService
	) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}

		this.updateDps();
		this.upgradesBySlotGroup = {};
		this.updateUpgradeStatus();

		this.characterService.getCharacter(this.selectedCharacterId).subscribe(character => {
			this.selectedCharacter = character;
		});
	}

	onEquipmentChanged() {
		this.updateDps();
		this.updateUpgradeStatus();
	}

	onItemFilterChanged(itemFilter: ItemFilter) {
		Object.keys(itemFilter).forEach(key => {
			this.itemFilter[key as keyof ItemFilter] = itemFilter[key as keyof ItemFilter];
		});

		this.updateUpgradeStatus();
	}

	onBuffsChanged() {
		this.updateDps();
	}

	updateDps() {
		this.dpsBar?.updateDps();
	}

	updateUpgradeStatus() {
		for (let slotGroup of Object.values(ItemSlotGroup)) {
			this.upgradeService.getUpgrades(this.selectedCharacterId!, slotGroup, this.itemFilter).subscribe(upgrades => {
				this.upgradesBySlotGroup[slotGroup] = upgrades;
			});
		}
	}
}
