import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { formatCharacterId, parseCharacterId } from 'src/app/modules/character/model/CharacterId';
import { CharacterSelectionOptions } from 'src/app/modules/character/model/CharacterSelectionOptions';
import { DropdownSelectValueFormatter, ElementComparatorFn } from 'src/app/modules/shared/components/dropdown-select/dropdown-select.component';
import { EnemyType } from 'src/app/modules/shared/model/character/EnemyType';
import { LevelDifference } from 'src/app/modules/shared/model/character/LevelDifference';
import { Phase } from 'src/app/modules/shared/model/character/Phase';
import { PhaseId } from 'src/app/modules/shared/model/character/PhaseId';
import { ProfileInfo } from '../../model/ProfileInfo';
import { ProfileService } from '../../services/profile.service';

@Component({
	selector: 'app-character-select',
	templateUrl: './character-select.component.html',
	styleUrl: './character-select.component.css'
})
export class CharacterSelectComponent implements OnChanges {
	@Input({ required: true }) selectedProfileId!: string;
	@Input({ required: true }) selectedCharacterId!: string;
	@Output() characterSelected = new EventEmitter<string>();

	characterSelectionOptions?: CharacterSelectionOptions;

	phaseId?: PhaseId;
	level?: number;
	enemyTypeId?: string;
	enemyLevelDiff?: number;

	constructor(private profileService: ProfileService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedProfileId']) {
			return;
		}

		this.profileService.getCharacterSelectionOptions(this.selectedProfileId).subscribe(characterSelectionOptions => {
			this.characterSelectionOptions = characterSelectionOptions;

			const characterId = this.pickCharacterId();

			if (this.selectedCharacterId !== characterId) {
				this.selectedCharacterId = characterId;
				this.setSelectBoxes();
				this.characterSelected.emit(characterId);
			} else {
				this.setSelectBoxes();
			}
		});
	}

	private pickCharacterId() {
		if (this.selectedCharacterId && this.selectedProfileId === parseCharacterId(this.selectedCharacterId).profileId) {
			return this.selectedCharacterId;
		}
		return this.characterSelectionOptions!.lastModifiedCharacterId;
	}

	private setSelectBoxes() {
		const parts = parseCharacterId(this.selectedCharacterId);

		this.phaseId = parts.phaseId;
		this.level = parts.level;
		this.enemyTypeId = parts.enemyTypeId;
		this.enemyLevelDiff = parts.enemyLevelDiff;
	}

	onPhaseChange(phase: Phase) {
		this.phaseId = phase.id;
		this.level = phase.maxLevel;
		this.loadFilteredCharacter();
	}

	onLevelChange(level: number) {
		this.level = level;
		this.loadFilteredCharacter();
	}

	onEnemyTypeChange(enemyType: EnemyType) {
		this.enemyTypeId = enemyType.id;
		this.loadFilteredCharacter();
	}

	onEnemyLevelDifferenceChange(enemyLevelDifference: LevelDifference) {
		this.enemyLevelDiff = enemyLevelDifference.id;
		this.loadFilteredCharacter();
	}

	loadFilteredCharacter() {
		const newCharacterId = formatCharacterId({
			profileId: this.selectedProfileId,
			phaseId: this.phaseId!,
			level: this.level!,
			enemyTypeId: this.enemyTypeId!,
			enemyLevelDiff: this.enemyLevelDiff!
		});

		this.selectedCharacterId = newCharacterId;
		this.setSelectBoxes();
		this.characterSelected.emit(newCharacterId);
	}

	readonly phaseFormatter = new PhaseFormatter();
	readonly levelFormatter = new LevelFormatter();
	readonly enemyTypeFormatter = new EnemyTypeFormatter();
	readonly levelDifferenceFormatter = new LevelDifferenceFormatter();
	readonly sortProfiles = sortProfiles;

	getPhase() {
		return this.characterSelectionOptions!.phases.find(x => x.id.toLowerCase() === this.phaseId?.toLowerCase());
	}

	getLevels() {
		return [ this.getPhase()!.maxLevel ];
	}

	getEnemyType() {
		return this.characterSelectionOptions!.enemyTypes.find(x => x.id.toLowerCase() === this.enemyTypeId?.toLowerCase());
	}

	getEnemyLevelDiff() {
		return this.characterSelectionOptions!.enemyLevelDiffs.find(x => x.id === this.enemyLevelDiff);
	}

	readonly enemyTypeComparator: ElementComparatorFn<EnemyType> = (a, b) => a.name.localeCompare(b.name);
}

function sortProfiles(profileList: ProfileInfo[]) {
	return profileList.sort((a, b) => a.profileName.localeCompare(b.profileName));
}

class PhaseFormatter implements DropdownSelectValueFormatter<Phase> {
	formatElement(value: Phase) {
		return value.name;
	}

	formatSelection(value: Phase) {
		return '<span class="character-select-bar-data">' + value.name + '</span>';
	}

	formatTooltip(value?: Phase) {
		return '';
	}

	trackKey(value: Phase) {
		return value.id;
	}
}

class LevelFormatter implements DropdownSelectValueFormatter<number> {
	formatElement(value: number) {
		return '' + value;
	}

	formatSelection(value: number) {
		return '<span class="character-select-bar-data">' + value + '</span>';
	}

	formatTooltip(value?: number) {
		return '';
	}

	trackKey(value: number) {
		return '' + value;
	}
}

class EnemyTypeFormatter implements DropdownSelectValueFormatter<EnemyType> {
	formatElement(value: EnemyType) {
		return value.name;
	}

	formatSelection(value: EnemyType) {
		return '<span class="character-select-bar-data">' + value.name + '</span>';
	}

	formatTooltip(value?: EnemyType) {
		return '';
	}

	trackKey(value: EnemyType) {
		return value.id;
	}
}

class LevelDifferenceFormatter implements DropdownSelectValueFormatter<LevelDifference> {
	formatElement(value: LevelDifference) {
		return value.name;
	}

	formatSelection(value: LevelDifference) {
		return '<span class="character-select-bar-data">' + value.name + '</span>';
	}

	formatTooltip(value?: LevelDifference) {
		return '';
	}

	trackKey(value: LevelDifference) {
		return '' + value.id;
	}
}
