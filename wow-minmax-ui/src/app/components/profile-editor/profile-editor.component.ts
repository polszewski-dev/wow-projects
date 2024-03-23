import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { Character } from 'src/app/modules/character/model/Character';
import { BuffListType } from 'src/app/modules/character/model/buff/BuffListType';
import { ItemFilter } from 'src/app/modules/character/model/equipment/ItemFilter';
import { ItemSlotGroup } from 'src/app/modules/character/model/upgrade/ItemSlotGroup';
import { Upgrade } from 'src/app/modules/character/model/upgrade/Upgrade';
import { CharacterService } from 'src/app/modules/character/services/character.service';
import { UpgradeService } from 'src/app/modules/character/services/upgrade.service';
import { RotationStats } from 'src/app/modules/statistics/model/RotationStats';
import { StatsService } from 'src/app/modules/statistics/services/stats.service';

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
	upgradesBySlotGroup: { [key in ItemSlotGroup]?: Upgrade[] } = {};

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

		this.characterService.getCharacter(this.selectedCharacterId).subscribe((character: Character) => {
			this.selectedCharacter = character;
		});
	}

	onEquipmentChanged(): void {
		this.updateDps();
		this.updateUpgradeStatus();
	}

	onItemFilterChanged(): void {
		this.updateUpgradeStatus();
	}

	onBuffsChanged(): void {
		this.updateDps();
	}

	updateDps(): void {
		this.statsService.getRotationStats(this.selectedCharacterId!).subscribe((rotationStats: RotationStats) => {
			this.previousRotationStats = this.rotationStats;
			this.rotationStats = rotationStats;
		})
	}

	updateUpgradeStatus(): void {
		for (let slotGroup of Object.values(ItemSlotGroup)) {
			this.upgradeService.getUpgrades(this.selectedCharacterId!, slotGroup, this.itemFilter).subscribe((upgrades: Upgrade[]) => {
				this.upgradesBySlotGroup[slotGroup] = upgrades;
			});
		}
	}
}
