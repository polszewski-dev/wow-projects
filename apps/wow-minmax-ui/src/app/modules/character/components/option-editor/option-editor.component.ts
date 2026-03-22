import { Component, Input } from '@angular/core';
import { Action, createSelector, Selector, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { Description } from 'src/app/modules/shared/model/Description';
import { copyOptionGroup, OptionGroup } from '../../model/OptionGroup';
import { OptionStatus } from '../../model/OptionStatus';
import { CharacterModuleState } from '../../state/character-module.state';
import { selectPlayerId } from '../../state/character/character.selectors';

@Component({
	selector: 'app-option-editor',
	templateUrl: './option-editor.component.html',
	styleUrl: './option-editor.component.css'
})
export class OptionEditorComponent<T extends Description & { id: number }> {
	@Input({ required: true })
	selector!: Selector<CharacterModuleState, OptionGroup<T>[]>;

	@Input({ required: true })
	actionGenerator!: (playerId: string, status: OptionStatus<T>) => Action;

	@Input({ required: true })
	elementPrefix!: string;

	data$!: Observable<DataView<T>>;

	constructor(private store: Store<CharacterModuleState>) {}

	ngOnInit(): void {
		this.data$ = this.store.select(createDataSelector(this.selector));
	}

	onChange(playerId: string, status: OptionStatus<T>) {
		this.store.dispatch(this.actionGenerator(playerId, status));
	}
}

type DataView<T> = {
	playerId: string;
	statuses: OptionGroup<T>[]
} | null;

function createDataSelector<T>(selector: Selector<CharacterModuleState, OptionGroup<T>[]>) {
	return createSelector(
		selectPlayerId,
		selector,
		(playerId, statuses): DataView<T> => {
			if (!playerId) {
				return null;
			}

			return {
				playerId,
				statuses: statuses.map(copyOptionGroup)
			};
		}
	);
}
