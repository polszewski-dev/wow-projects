import { Component } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { Consumable } from '../../model/consumable/Consumable';
import { CharacterModuleState } from '../../state/character-module.state';
import { enableConsumable } from '../../state/character/character.actions';
import { selectCharacterId, selectConsumables } from '../../state/character/character.selectors';

@Component({
	selector: 'app-consumable-editor',
	templateUrl: './consumable-editor.component.html',
	styleUrls: ['./consumable-editor.component.css']
})
export class ConsumableEditorComponent {
	data$ = this.store.select(dataSelector);

	constructor(private store: Store<CharacterModuleState>) {}

	onChange(characterId: string, consumable: Consumable) {
		this.store.dispatch(enableConsumable({ characterId, consumable }));
	}
}

type DataView = {
	characterId: string;
	consumables: Consumable[]
} | null;

const dataSelector = createSelector(
	selectCharacterId,
	selectConsumables,
	(characterId, consumables): DataView => {
		if (!characterId) {
			return null;
		}

		return {
			characterId,
			consumables: consumables.map(x => ({ ...x }))
		};
	}
);
