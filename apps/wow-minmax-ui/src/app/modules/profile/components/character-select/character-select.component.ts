import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { formatCharacterId, parseCharacterId } from 'src/app/modules/character/model/CharacterId';
import { CharacterSelectionOptions } from 'src/app/modules/character/model/CharacterSelectionOptions';
import { DropdownSelectValueFormatter, ElementComparatorFn } from 'src/app/modules/shared/components/dropdown-select/dropdown-select.component';
import { EnemyType } from 'src/app/modules/shared/model/character/EnemyType';
import { LevelDifference } from 'src/app/modules/shared/model/character/LevelDifference';
import { Phase } from 'src/app/modules/shared/model/character/Phase';
import { PhaseId } from 'src/app/modules/shared/model/character/PhaseId';
import { ProfileService } from '../../services/profile.service';

@Component({
	selector: 'app-character-select',
	templateUrl: './character-select.component.html',
	styleUrl: './character-select.component.css'
})
export class CharacterSelectComponent implements OnInit, OnChanges {
	@Input({ required: true }) selectedProfileId!: string;
	@Input({ required: true }) selectedCharacterId!: string;
	@Output() characterSelected = new EventEmitter<string>();

	characterSelectionOptions?: CharacterSelectionOptions;

	readonly form = new FormGroup<CharacterFilterForm>({
		phase: new FormControl(null, Validators.required),
		level: new FormControl(null, Validators.required),
		enemyType: new FormControl(null, Validators.required),
		levelDiff: new FormControl(null, Validators.required),
	});

	constructor(private profileService: ProfileService) {}

	ngOnInit(): void {
		this.form.controls.phase.valueChanges.subscribe(() => this.onFilterChange('phase'));
		this.form.controls.level.valueChanges.subscribe(() => this.onFilterChange('level'));
		this.form.controls.enemyType.valueChanges.subscribe(() => this.onFilterChange('enemyType'));
		this.form.controls.levelDiff.valueChanges.subscribe(() => this.onFilterChange('levelDiff'));
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (changes['selectedProfileId']) {
			this.profileService.getCharacterSelectionOptions(this.selectedProfileId).subscribe(characterSelectionOptions => {
				this.characterSelectionOptions = characterSelectionOptions;
				this.setSelectBoxes();
			});
		}

		if (changes['selectedCharacterId']) {
			this.setSelectBoxes();
		}
	}

	private setSelectBoxes() {
		if (!this.characterSelectionOptions) {
			return;
		}

		const parts = parseCharacterId(this.selectedCharacterId);

		this.form.setValue({
			phase: this.getPhase(parts.phaseId)!,
			level: parts.level,
			enemyType: this.getEnemyType(parts.enemyTypeId)!,
			levelDiff: this.getEnemyLevelDiff(parts.enemyLevelDiff)!
		}, { emitEvent: false });
	}

	private onFilterChange(field: keyof CharacterFilterForm) {
		this.form.updateValueAndValidity({ emitEvent: false });

		if (field === 'phase') {
			this.form.patchValue({ level: this.form.value.phase?.maxLevel }, { emitEvent: false });
		}

		const newCharacterId = formatCharacterId({
			profileId: this.selectedProfileId,
			phaseId: this.form.value.phase!.id,
			level: this.form.value.level!,
			enemyTypeId: this.form.value.enemyType!.id,
			enemyLevelDiff: this.form.value.levelDiff!.id
		});

		this.selectedCharacterId = newCharacterId;
		this.characterSelected.emit(newCharacterId);
	}

	readonly phaseFormatter = new PhaseFormatter();
	readonly levelFormatter = new LevelFormatter();
	readonly enemyTypeFormatter = new EnemyTypeFormatter();
	readonly levelDifferenceFormatter = new LevelDifferenceFormatter();

	private getPhase(phaseId: PhaseId) {
		return this.characterSelectionOptions!.phases.find(x => x.id.toLowerCase() === phaseId.toLowerCase());
	}

	private getEnemyType(enemyTypeId: string) {
		return this.characterSelectionOptions!.enemyTypes.find(x => x.id.toLowerCase() === enemyTypeId.toLowerCase());
	}

	private getEnemyLevelDiff(enemyLevelDiff: number) {
		return this.characterSelectionOptions!.enemyLevelDiffs.find(x => x.id === enemyLevelDiff);
	}

	get levels() {
		const phase = this.form.value.phase;
		return phase ? [ phase!.maxLevel ] : [];
	}

	readonly enemyTypeComparator: ElementComparatorFn<EnemyType> = (a, b) => a.name.localeCompare(b.name);
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

type CharacterFilterForm = {
	phase: FormControl<Phase | null>,
	level: FormControl<number | null>,
	enemyType: FormControl<EnemyType | null>,
	levelDiff: FormControl<LevelDifference | null>,
}
