import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { CharacterModuleState } from 'src/app/modules/character/state/character-module.state';
import { selectCharacterId } from '../../modules/character/state/character/character.selectors';

@Component({
	selector: 'app-main',
	templateUrl: './main.component.html',
	styleUrls: ['./main.component.css']
})
export class MainComponent {
	selectedCharacterId$ = this.store.select(selectCharacterId);

	constructor(private store: Store<CharacterModuleState>) {}
}
