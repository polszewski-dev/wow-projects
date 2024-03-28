import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { formatCharacterId, parseCharacterId } from '../../../character/model/CharacterId';
import { CharacterSelectionOptions } from '../../../character/model/CharacterSelectionOptions';
import { DropdownSelectValueFormatter, ElementComparatorFn } from '../../../shared/components/dropdown-select/dropdown-select.component';
import { EnemyType } from '../../../shared/model/character/EnemyType';
import { LevelDifference } from '../../../shared/model/character/LevelDifference';
import { Phase } from '../../../shared/model/character/Phase';
import { PhaseId } from '../../../shared/model/character/PhaseId';
import { ProfileInfo } from '../../model/ProfileInfo';
import { ProfileService } from '../../services/profile.service';

@Component({
	selector: 'app-profile-select',
	templateUrl: './profile-select.component.html',
	styleUrls: ['./profile-select.component.css']
})
export class ProfileSelectComponent implements OnChanges {
	@Input() profileList: ProfileInfo[] = [];
	@Input() selectedCharacterId?: string;
	@Output() characterSelected = new EventEmitter<string>();

	characterSelectionOptions?: CharacterSelectionOptions;

	profileId?: string;
	phaseId?: PhaseId;
	level?: number;
	enemyTypeId?: string;
	enemyLevelDiff?: number;

	constructor(private profileService: ProfileService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		if (!this.selectedCharacterId) {
			return;
		}

		const profileId = parseCharacterId(this.selectedCharacterId).profileId;

		this.setSelectedProfile(profileId, this.selectedCharacterId);
	}

	onProfileChange(profileId: string) {
		this.setSelectedProfile(profileId, undefined);
	}

	setSelectedProfile(profileId: string, characterId?: string) {
		this.profileService.getCharacterSelectionOptions(profileId).subscribe(characterSelectionOptions => {
			this.characterSelectionOptions = characterSelectionOptions;
			this.setSelectedCharacterId(characterId || characterSelectionOptions.lastModifiedCharacterId);
		});
	}

	setSelectedCharacterId(selectedCharacterId: string) {
		this.selectedCharacterId = selectedCharacterId;

		const parts = parseCharacterId(selectedCharacterId);

		this.profileId = parts.profileId;
		this.phaseId = parts.phaseId;
		this.level = parts.level;
		this.enemyTypeId = parts.enemyTypeId;
		this.enemyLevelDiff = parts.enemyLevelDiff;

		this.characterSelected.emit(selectedCharacterId);
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
			profileId: this.profileId!,
			phaseId: this.phaseId!,
			level: this.level!,
			enemyTypeId: this.enemyTypeId!,
			enemyLevelDiff: this.enemyLevelDiff!
		});
		this.setSelectedCharacterId(newCharacterId);
	}

	sortProfiles = sortProfiles;

	readonly phaseFormatter = new PhaseFormatter();
	readonly levelFormatter = new LevelFormatter();
	readonly enemyTypeFormatter = new EnemyTypeFormatter();
	readonly levelDifferenceFormatter = new LevelDifferenceFormatter();

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
}
