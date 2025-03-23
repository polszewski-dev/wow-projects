import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { combineLatest, filter, map, of, switchMap, tap } from 'rxjs';
import { formatCharacterId, parseCharacterId } from 'src/app/modules/character/model/CharacterId';
import { CharacterSelectionOptions } from 'src/app/modules/character/model/CharacterSelectionOptions';
import { CharacterModuleState } from 'src/app/modules/character/state/character-module.state';
import { selectCharacter } from 'src/app/modules/character/state/character/character.actions';
import { selectCharacterId } from 'src/app/modules/character/state/character/character.selectors';
import { DropdownSelectValueFormatter, ElementComparatorFn } from 'src/app/modules/shared/components/dropdown-select/dropdown-select.component';
import { EnemyType } from 'src/app/modules/shared/model/character/EnemyType';
import { LevelDifference } from 'src/app/modules/shared/model/character/LevelDifference';
import { Phase } from 'src/app/modules/shared/model/character/Phase';
import { PhaseId } from 'src/app/modules/shared/model/character/PhaseId';
import { ProfileService } from '../../services/profile.service';
import { ProfileModuleState } from '../../state/profile-module.state';
import { selectSelectedProfile } from '../../state/profile.selectors';

@Component({
	selector: 'app-character-select',
	templateUrl: './character-select.component.html',
	styleUrl: './character-select.component.css'
})
export class CharacterSelectComponent implements OnInit {
	readonly form = new FormGroup<CharacterFilterForm>({
		profileId: new FormControl(null, Validators.required),
		phase: new FormControl(null, Validators.required),
		level: new FormControl(null, Validators.required),
		enemyType: new FormControl(null, Validators.required),
		levelDiff: new FormControl(null, Validators.required),
	});

	data$ = combineLatest({
		profile: this.store.select(selectSelectedProfile),
		characterId: this.store.select(selectCharacterId),
	}).pipe(
		// p and c have either matching profileId or both are null
		filter(({ profile, characterId }) => (!!profile && !!characterId && profile.profileId === parseCharacterId(characterId).profileId) || (!profile && !characterId)),
		// attach options to the result
		switchMap(({ profile, characterId }) => profile
			? this.profileService.getCharacterSelectionOptions(profile.profileId!).pipe(
				map(characterSelectionOptions => ({ profile, characterId, characterSelectionOptions }))
			)
			: of({ profile: null, characterId: null, characterSelectionOptions: null })
		),
		tap(({ profile, characterId, characterSelectionOptions }) => {
			if (profile) {
				this.setSelectBoxes(characterId!, characterSelectionOptions!);
			}
		})
	);

	constructor(
		private profileService: ProfileService,
		private store: Store<ProfileModuleState & CharacterModuleState>
	) {}

	ngOnInit(): void {
		this.form.controls.phase.valueChanges.subscribe(() => this.onFilterChange('phase'));
		this.form.controls.level.valueChanges.subscribe(() => this.onFilterChange('level'));
		this.form.controls.enemyType.valueChanges.subscribe(() => this.onFilterChange('enemyType'));
		this.form.controls.levelDiff.valueChanges.subscribe(() => this.onFilterChange('levelDiff'));
	}

	private setSelectBoxes(selectedCharacterId: string, characterSelectionOptions: CharacterSelectionOptions) {
		const parts = parseCharacterId(selectedCharacterId);

		this.form.setValue({
			profileId: parts.profileId,
			phase: this.getPhase(parts.phaseId, characterSelectionOptions)!,
			level: parts.level,
			enemyType: this.getEnemyType(parts.enemyTypeId, characterSelectionOptions)!,
			levelDiff: this.getEnemyLevelDiff(parts.enemyLevelDiff, characterSelectionOptions)!
		}, { emitEvent: false });
	}

	private onFilterChange(field: keyof CharacterFilterForm) {
		this.form.updateValueAndValidity({ emitEvent: false });

		if (field === 'phase') {
			this.form.patchValue({ level: this.form.value.phase?.maxLevel }, { emitEvent: false });
		}

		const newCharacterId = formatCharacterId({
			profileId: this.form.value.profileId!,
			phaseId: this.form.value.phase!.id,
			level: this.form.value.level!,
			enemyTypeId: this.form.value.enemyType!.id,
			enemyLevelDiff: this.form.value.levelDiff!.id
		});

		this.store.dispatch(selectCharacter({ characterId: newCharacterId }));
	}

	readonly phaseFormatter = new PhaseFormatter();
	readonly levelFormatter = new LevelFormatter();
	readonly enemyTypeFormatter = new EnemyTypeFormatter();
	readonly levelDifferenceFormatter = new LevelDifferenceFormatter();

	private getPhase(phaseId: PhaseId, characterSelectionOptions: CharacterSelectionOptions) {
		return characterSelectionOptions!.phases.find(x => x.id.toLowerCase() === phaseId.toLowerCase());
	}

	private getEnemyType(enemyTypeId: string, characterSelectionOptions: CharacterSelectionOptions) {
		return characterSelectionOptions!.enemyTypes.find(x => x.id.toLowerCase() === enemyTypeId.toLowerCase());
	}

	private getEnemyLevelDiff(enemyLevelDiff: number, characterSelectionOptions: CharacterSelectionOptions) {
		return characterSelectionOptions!.enemyLevelDiffs.find(x => x.id === enemyLevelDiff);
	}

	get levels() {
		const phase = this.form.value.phase;
		if (!phase) {
			return [];
		}
		const result = [];
		for (let level = phase!.maxLevel; level >= 10; level -= 2) {
			result.push(level);
		}
		return result;
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
	profileId: FormControl<string | null>,
	phase: FormControl<Phase | null>,
	level: FormControl<number | null>,
	enemyType: FormControl<EnemyType | null>,
	levelDiff: FormControl<LevelDifference | null>,
}
