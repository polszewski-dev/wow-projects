import { Component } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { ConsumableStatus } from '../../model/consumable/ConsumableStatus';
import { CharacterModuleState } from '../../state/character-module.state';
import { changeConsumableStatus } from '../../state/character/character.actions';
import { selectCharacterId, selectConsumableStatuses } from '../../state/character/character.selectors';

@Component({
	selector: 'app-consumable-editor',
	templateUrl: './consumable-editor.component.html',
	styleUrls: ['./consumable-editor.component.css']
})
export class ConsumableEditorComponent {
	data$ = this.store.select(dataSelector);

	constructor(private store: Store<CharacterModuleState>) {}

	onChange(characterId: string, consumableStatus: ConsumableStatus) {
		this.store.dispatch(changeConsumableStatus({ characterId, consumableStatus }));
	}
}

type DataView = {
	characterId: string;
	consumableStatuses: ConsumableStatus[]
} | null;

const dataSelector = createSelector(
	selectCharacterId,
	selectConsumableStatuses,
	(characterId, consumableStatuses): DataView => {
		if (!characterId) {
			return null;
		}

		return {
			characterId,
			consumableStatuses: consumableStatuses.map(x => ({ ...x }))
		};
	}
);
