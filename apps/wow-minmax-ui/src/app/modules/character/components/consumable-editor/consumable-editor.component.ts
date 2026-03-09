import { Component } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { ConsumableStatus } from '../../model/consumable/ConsumableStatus';
import { CharacterModuleState } from '../../state/character-module.state';
import { changeConsumableStatus } from '../../state/character/character.actions';
import { selectPlayerId, selectConsumableStatuses } from '../../state/character/character.selectors';

@Component({
	selector: 'app-consumable-editor',
	templateUrl: './consumable-editor.component.html',
	styleUrls: ['./consumable-editor.component.css']
})
export class ConsumableEditorComponent {
	data$ = this.store.select(dataSelector);

	constructor(private store: Store<CharacterModuleState>) {}

	onChange(playerId: string, consumableStatus: ConsumableStatus) {
		this.store.dispatch(changeConsumableStatus({ playerId, consumableStatus }));
	}
}

type DataView = {
	playerId: string;
	consumableStatuses: ConsumableStatus[]
} | null;

const dataSelector = createSelector(
	selectPlayerId,
	selectConsumableStatuses,
	(playerId, consumableStatuses): DataView => {
		if (!playerId) {
			return null;
		}

		return {
			playerId,
			consumableStatuses: consumableStatuses.map(x => ({ ...x }))
		};
	}
);
