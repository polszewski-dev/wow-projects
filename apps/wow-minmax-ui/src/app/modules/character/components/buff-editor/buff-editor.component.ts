import { Component, Input, OnInit } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { BuffListType } from '../../model/buff/BuffListType';
import { BuffStatus } from '../../model/buff/BuffStatus';
import { CharacterModuleState } from '../../state/character-module.state';
import { changeBuffStatus } from '../../state/character/character.actions';
import { selectBuffList, selectCharacterId } from '../../state/character/character.selectors';

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

	onChange(characterId: string, buffStatus: BuffStatus) {
		this.store.dispatch(changeBuffStatus({ characterId, buffListType: this.buffListType, buffStatus }));
	}
}

type DataView = {
	characterId: string;
	buffStatusList: BuffStatus[]
} | null;

function createDataSelector(buffListType: BuffListType) {
	return createSelector(
		selectCharacterId,
		selectBuffList(buffListType),
		(characterId, buffStatusList): DataView => {
			if (!characterId) {
				return null;
			}

			return {
				characterId,
				buffStatusList: buffStatusList.map(x => ({ ...x }))
			};
		}
	);
}
