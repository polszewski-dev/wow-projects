import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { Character } from '../../../character/model/Character';
import { BuffListType } from '../../../character/model/buff/BuffListType';
import { ItemFilter } from '../../../character/model/equipment/ItemFilter';
import { ItemSlotGroup } from '../../../character/model/upgrade/ItemSlotGroup';
import { Upgrade } from '../../../character/model/upgrade/Upgrade';
import { CharacterService } from '../../../character/services/character.service';
import { UpgradeService } from '../../../character/services/upgrade.service';
import { RotationStats } from '../../../statistics/model/RotationStats';
import { StatsService } from '../../../statistics/services/stats.service';

@Component({
	selector: 'app-profile-editor',
	templateUrl: './profile-editor.component.html',
	styleUrls: ['./profile-editor.component.css']
})
export class ProfileEditorComponent implements OnChanges {
	@Input() selectedCharacterId!: string;
	selectedCharacter?: Character;
	rotationStats?: RotationStats;
	previousRotationStats?: RotationStats;
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
			private statsService: StatsService,
			private upgradeService: UpgradeService
	) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.rotationStats = undefined;
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

	onItemFilterChanged() {
		this.updateUpgradeStatus();
	}

	onBuffsChanged() {
		this.updateDps();
	}

	updateDps() {
		this.statsService.getRotationStats(this.selectedCharacterId!).subscribe(rotationStats => {
			this.previousRotationStats = this.rotationStats;
			this.rotationStats = rotationStats;
		})
	}

	updateUpgradeStatus() {
		for (let slotGroup of Object.values(ItemSlotGroup)) {
			this.upgradeService.getUpgrades(this.selectedCharacterId!, slotGroup, this.itemFilter).subscribe(upgrades => {
				this.upgradesBySlotGroup[slotGroup] = upgrades;
			});
		}
	}
}
