import { Component, Input, OnInit } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { BuffListType } from '../../model/buff/BuffListType';
import { BuffStatus } from '../../model/buff/BuffStatus';
import { CharacterModuleState } from '../../state/character-module.state';
import { changeBuffStatus } from '../../state/character/character.actions';
import { selectBuffList, selectPlayerId } from '../../state/character/character.selectors';

@Component({
	selector: 'app-buff-editor',
	templateUrl: './buff-editor.component.html',
	styleUrls: ['./buff-editor.component.css']
})
export class BuffEditorComponent implements OnInit {
	@Input({ required: true }) buffListType!: BuffListType;

	data$!: Observable<DataView>;

	constructor(private store: Store<CharacterModuleState>) {}

	ngOnInit(): void {
		this.data$ = this.store.select(createDataSelector(this.buffListType));
	}

	onChange(playerId: string, buffStatus: BuffStatus) {
		this.store.dispatch(changeBuffStatus({ playerId, buffListType: this.buffListType, buffStatus }));
	}
}

type DataView = {
	playerId: string;
	buffStatusList: BuffStatus[]
} | null;

function createDataSelector(buffListType: BuffListType) {
	return createSelector(
		selectPlayerId,
		selectBuffList(buffListType),
		(playerId, buffStatusList): DataView => {
			if (!playerId) {
				return null;
			}

			return {
				playerId,
				buffStatusList: buffStatusList.map(x => ({ ...x }))
			};
		}
	);
}
