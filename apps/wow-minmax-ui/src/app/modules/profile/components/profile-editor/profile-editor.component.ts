import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { CharacterModuleState } from 'src/app/modules/character/state/character-module.state';
import { selectCharacter, selectPlayerId } from 'src/app/modules/character/state/character/character.selectors';
import { BuffListType } from '../../../character/model/buff/BuffListType';

@Component({
	selector: 'app-profile-editor',
	templateUrl: './profile-editor.component.html',
	styleUrls: ['./profile-editor.component.css']
})
export class ProfileEditorComponent {
	selectedPlayerId$ = this.store.select(selectPlayerId);
	dataLoaded$ = this.store.select(selectCharacter);

	readonly CHARACTER_BUFF = BuffListType.CHARACTER_BUFF;
	readonly TARGET_DEBUFF = BuffListType.TARGET_DEBUFF;

	constructor(private store: Store<CharacterModuleState>) {}
}
