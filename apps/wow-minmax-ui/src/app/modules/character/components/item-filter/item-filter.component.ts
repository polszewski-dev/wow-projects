import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { createSelector, Store } from '@ngrx/store';
import { filter, tap } from 'rxjs';
import { CharacterModuleState } from '../../state/character-module.state';
import { selectCharacterId } from '../../state/character/character.selectors';
import { selectEquipmentOptions } from '../../state/equipment-options/equipment-options.selectors';
import { updateItemFilter } from '../../state/upgrades/upgrades.actions';
import { selectItemFilter } from '../../state/upgrades/upgrades.selectors';

@Component({
	selector: 'app-item-filter',
	templateUrl: './item-filter.component.html',
	styleUrls: ['./item-filter.component.css']
})
export class ItemFilterComponent {
	readonly form = this.formBuilder.nonNullable.group({
		heroics: false,
		raids: false,
		worldBosses: false,
		pvpItems: false,
		greens: false,
		legendaries: false
	});

	characterId!: string;

	$data = this.store.select(dataSelector).pipe(
		filter(x => !!x),
		tap(x => this.characterId = x!.characterId),
		tap(x => this.form.patchValue(x!.itemFilter, { emitEvent: false }))
	)

	constructor(
		private store: Store<CharacterModuleState>,
		private formBuilder: FormBuilder
	) {}

	ngOnInit(): void {
		this.form.valueChanges.subscribe(value => {
			this.store.dispatch(updateItemFilter({ characterId: this.characterId, itemFilter: value }));
		});
	}
}

const dataSelector = createSelector(
	selectCharacterId,
	selectItemFilter,
	selectEquipmentOptions,
	(characterId, itemFilter, equipmentOptions) => {
		if (!characterId || !equipmentOptions) {
			return null;
		}

		return {
			characterId,
			itemFilter,
			equipmentOptions
		}
	}
);
