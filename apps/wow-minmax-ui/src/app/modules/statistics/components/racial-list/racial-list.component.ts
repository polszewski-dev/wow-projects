import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { CharacterModuleState } from 'src/app/modules/character/state/character-module.state';
import { selectCharacter } from '../../../character/state/character/character.selectors';

@Component({
	selector: 'app-racial-list',
	templateUrl: './racial-list.component.html',
	styleUrls: ['./racial-list.component.css']
})
export class RacialListComponent {
	character$ = this.store.select(selectCharacter);

	constructor(private store: Store<CharacterModuleState>) {}
}
