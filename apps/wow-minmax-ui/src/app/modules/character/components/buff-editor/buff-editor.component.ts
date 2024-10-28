import { Component, Input, OnInit } from '@angular/core';
import { createSelector, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { Buff } from '../../model/buff/Buff';
import { BuffListType } from '../../model/buff/BuffListType';
import { CharacterModuleState } from '../../state/character-module.state';
import { enableBuff } from '../../state/character/character.actions';
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

	onChange(characterId: string, buff: Buff) {
		this.store.dispatch(enableBuff({ characterId, buffListType: this.buffListType, buff }));
	}
}

type DataView = {
	characterId: string;
	buffList: Buff[]
} | null;

function createDataSelector(buffListType: BuffListType) {
	return createSelector(
		selectCharacterId,
		selectBuffList(buffListType),
		(characterId, buffList): DataView => {
			if (!characterId) {
				return null;
			}

			return {
				characterId,
				buffList: buffList.map(x => ({ ...x }))
			};
		}
	);
}
